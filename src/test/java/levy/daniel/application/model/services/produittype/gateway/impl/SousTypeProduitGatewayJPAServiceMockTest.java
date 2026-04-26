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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
     * "creer(null) - jette ExceptionAppliParamNull (contrat du port)"
     */
    public static final String DN_CREER_NULL =
            "creer(null) - jette ExceptionAppliParamNull (contrat du port)";

    /**
     * "creer(blank) - jette ExceptionAppliLibelleBlank (contrat du port)"
     */
    public static final String DN_CREER_BLANK =
            "creer(blank) - jette ExceptionAppliLibelleBlank (contrat du port)";
       
    /**
     * "servicesGateway-Rechercher"
     */
    public static final String TAG_RECHERCHER = "servicesGateway-Rechercher";
    
    /**
     * "servicesGateway-Pagination"
     */
    public static final String TAG_PAGINATION = "servicesGateway-Pagination";
    
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
     * <li>émet le message {@link SousTypeProduitGatewayIService#MESSAGE_CREER_KO_LIBELLE_BLANK} ;</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName(DN_CREER_BLANK)
    @Test
    public void testCreerLibelleBlank() {

        /* ARRANGE :
         * prépare un sous-type dont le libellé est blank,
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
     * <li>émet le message {@link SousTypeProduitGatewayIService#MESSAGE_CREER_KO_PARENT_NULL} ;</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(parent null) - jette ExceptionAppliParentNull (contrat du port)")
    @Test
    public void testCreerParentNull() {

        /* ARRANGE :
         * prépare un sous-type sans parent,
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
     * <li>n'appelle ni le DAO parent ni le DAO enfant.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)")
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
    @DisplayName("creer(parent ID null) - jette ExceptionTechniqueGatewayNonPersistent")
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
    @DisplayName("creer(parent absent DAO) - jette ExceptionTechniqueGatewayNonPersistent")
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
         * this.service.creer(stp) avec stp sans parent
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
     * <li>propage une cause non null ;</li>
     * <li>n'appelle pas save(...).</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(KO DAO parent message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testCreerParentDaoExceptionMessageNonNull() {

        /* ARRANGE :
         * prépare un parent persistant en apparence,
         * puis configure le DAO parent mocké avec Mockito
         * pour jeter une RuntimeException avec message non null.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final RuntimeException ex = new RuntimeException(LIBELLE_PARENT_1);
        
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
    @DisplayName("creer(KO DAO parent message null) - jette ExceptionTechniqueGateway")
    @Test
    public void testCreerParentDaoExceptionMessageNull() {

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
     * <p>garantit que creer(save retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle findById(...) sur le parent puis save(...).</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(save retourne null) - jette ExceptionTechniqueGateway")
    @Test
    public void testCreerSaveRetourneNull() {

        /* ARRANGE :
         * prépare un scénario où le parent existe,
         * mais où le DAO enfant mocké avec Mockito
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
         * vérifie que :
         * this.service.creer(stp) avec 
         * sousTypeProduitDaoJPA.save(...) qui retourne null
         * - jette une ExceptionTechniqueGateway
         * - avec un message MSG_ERREUR_TECH_KO_STOCKAGE 
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.creer(stp))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

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
     * <p>garantit que creer(DAO save jette RuntimeException avec message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle findById(...) sur le parent puis save(...).</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(KO DAO save message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testCreerDaoSaveExceptionMessageNonNull() {

        /* ARRANGE :
         * prépare un scénario où le parent existe,
         * puis configure le DAO enfant mocké avec Mockito
         * pour jeter une RuntimeException avec message non null.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        
        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké 
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final RuntimeException ex = new RuntimeException(LIBELLE_ENFANT_1);
        
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
         * - sousTypeProduitDaoJPA.save(...) a été appelé une fois.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que creer(DAO save jette RuntimeException avec message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle findById(...) sur le parent puis save(...).</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(KO DAO save message null) - jette ExceptionTechniqueGateway")
    @Test
    public void testCreerDaoSaveExceptionMessageNull() {

        /* ARRANGE :
         * prépare un scénario où le parent existe,
         * puis configure le DAO enfant mocké avec Mockito
         * pour jeter une RuntimeException sans message.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        
        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké 
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final RuntimeException ex = new RuntimeException((String) null);
        
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
     * <li>retourne le libellé enfant attendu ;</li>
     * <li>retourne le libellé parent attendu ;</li>
     * <li>appelle findById(...) sur le parent puis save(...).</li>
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
     * <p>garantit que creer(nominal) :</p>
     * <ul>
     * <li>retourne un objet métier non null ;</li>
     * <li>retourne le libellé enfant attendu ;</li>
     * <li>retourne un parent non null ;</li>
     * <li>retourne le libellé parent attendu ;</li>
     * <li>appelle findById(...) sur le parent puis save(...).</li>
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
     * <p>garantit que rechercherTous(findAll retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle une seule fois le DAO mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous(findAll retourne null) - jette ExceptionTechniqueGateway")
    @Test
    public void testRechercherTousFindAllRetourneNull() {

        /* ARRANGE :
         * configure le DAO mocké avec Mockito
         * pour que DAO.findAll() retourne null au lieu d'une liste.
         */
        when(this.sousTypeProduitDaoJPA.findAll()).thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que :
         * this.service.rechercherTous() avec DAO.findAll() retourne null
         * - jette une ExceptionTechniqueGateway
         * - avec un message MSG_ERREUR_TECH_KO_STOCKAGE
         */
        assertThatThrownBy(() -> this.service.rechercherTous())
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Vérifie que la méthode findAll()
         * du DAO mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).findAll();

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que rechercherTous(findAll vide) :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>retourne une liste vide ;</li>
     * <li>appelle une seule fois le DAO mocké avec Mockito.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous(findAll vide) - retourne une liste vide")
    @Test
    public void testRechercherTousFindAllVide() throws Exception {

        /* ARRANGE :
         * configure le DAO mocké avec Mockito
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
         * Vérifie que la méthode findAll()
         * du DAO sousTypeProduitDaoJPA mocké avec Mockito 
         * a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).findAll();

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que rechercherTous(KO DAO message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle une seule fois le DAO mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous(KO DAO message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testRechercherTousDaoExceptionMessageNonNull() {

        /* ARRANGE :
         * configure le DAO mocké avec Mockito
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
         * son préfixe contractuel et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable.getCause()).isSameAs(ex);

        /* 
         * Vérifie que la méthode findAll()
         * du DAO sousTypeProduitDaoJPA mocké avec Mockito 
         * a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).findAll();

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que rechercherTous(nominal) :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>filtre les valeurs null ;</li>
     * <li>dédoublonne les doublons fonctionnels ;</li>
     * <li>retourne une liste triée ;</li>
     * <li>conserve un parent non null sur les objets métier retournés.</li>
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
         * prépare une liste renvoyée par le DAO mocké avec Mockito
         * contenant :
         * - une valeur null à filtrer ;
         * - deux doublons fonctionnels sur le même libellé ;
         * - un ordre initial non trié.
         *
         * Ce scénario permet de vérifier en une seule fois
         * le filtrage, le tri et le dédoublonnage.
         */
        final TypeProduitJPA parentJPA 
        	= fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        final SousTypeProduitJPA e1 =
                fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_2, ID_1, parentJPA);
        final SousTypeProduitJPA e2 =
                fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);
        final SousTypeProduitJPA e3 =
                fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_3, parentJPA);

        final List<SousTypeProduitJPA> entities = new ArrayList<SousTypeProduitJPA>();
        entities.add(null);
        entities.add(e1);
        entities.add(e2);
        entities.add(e3);

        /* Condition du Mock typeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findAll() sur le DAO mocké 
         * retourne la liste entities.
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

        /* Vérifie ensuite que les doublons fonctionnels
         * ont bien été supprimés
         * et que l'ordre final est trié par libellé.
         */
        assertThat(retour)
            .extracting(SousTypeProduit::getSousTypeProduit)
            .containsExactly(LIBELLE_ENFANT_1, LIBELLE_ENFANT_2);

        /* Vérifie enfin que les objets métier retournés
         * conservent un parent non null et cohérent.
         */
        assertThat(retour.get(0).getTypeProduit()).isNotNull();
        assertThat(retour.get(0).getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_1);
        assertThat(retour.get(1).getTypeProduit()).isNotNull();
        assertThat(retour.get(1).getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_1);

        /* 
         * Vérifie que la méthode findAll()
         * du DAO sousTypeProduitDaoJPA mocké avec Mockito 
         * a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).findAll();

    } // __________________________________________________________________
    
    
    
    // ================== rechercherTousParPage ===========================
    
    
    
    /**
     * <div>
     * <p>garantit que rechercherTousParPage(null) :</p>
     * <ul>
     * <li>retourne un {@link ResultatPage} non null ;</li>
     * <li>retourne un contenu cohérent avec la page renvoyée par le DAO ;</li>
     * <li>retourne les métadonnées de pagination attendues ;</li>
     * <li>appelle une seule fois la méthode findAll(Pageable)
     * du DAO mocké avec Mockito.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(null) - applique la requête par défaut et retourne une page cohérente")
    @Test
    public void testRechercherTousParPageParamNull() throws Exception {

        /* ARRANGE :
         * configure le DAO mocké avec Mockito
         * pour que la méthode findAll(Pageable)
         * retourne une page contenant deux éléments.
         */
        final TypeProduitJPA parentJPA =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        final List<SousTypeProduitJPA> contenu =
                new ArrayList<SousTypeProduitJPA>();
        contenu.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_1, parentJPA));
        contenu.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_2, ID_2, parentJPA));

        final Page<SousTypeProduitJPA> page =
                new PageImpl<SousTypeProduitJPA>(
                        contenu,
                        PageRequest.of(PAGE_0, SIZE_5),
                        TOTAL_10);

        /* Condition du Mock typeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findAll(Pageable) sur le DAO mocké 
         * retourne une Page.
         */
        when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        /* ACT :
         * appelle this.service.rechercherTousParPage(null)
         * dans le scénario où la requête paginée est null.
         */
        final ResultatPage<SousTypeProduit> resultat =
                this.service.rechercherTousParPage(null);

        /* ASSERT :
         * vérifie que le service retourne bien
         * une enveloppe paginée exploitable et cohérente.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().hasSize(2);
        assertThat(resultat.getPageNumber()).isEqualTo(PAGE_0);
        assertThat(resultat.getPageSize()).isEqualTo(SIZE_5);
        assertThat(resultat.getTotalElements()).isEqualTo(TOTAL_10);

        /* Vérifie que le contenu métier retourné
         * correspond aux deux éléments de la page DAO.
         */
        assertThat(resultat.getContent())
            .extracting(SousTypeProduit::getSousTypeProduit)
            .containsExactly(LIBELLE_ENFANT_1, LIBELLE_ENFANT_2);

        /* Vérifie que la méthode findAll(Pageable)
         * du DAO sousTypeProduitDaoJPA mocké avec Mockito 
         * a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).findAll(any(Pageable.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que rechercherTousParPage(tris invalides) :</p>
     * <ul>
     * <li>retourne un {@link ResultatPage} non null ;</li>
     * <li>ignore les tris invalides sans échec ;</li>
     * <li>retourne un contenu cohérent avec la page renvoyée par le DAO ;</li>
     * <li>appelle une seule fois la méthode findAll(Pageable)
     * du DAO mocké avec Mockito.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(tris invalides) - ignore les tris invalides et reste nominal")
    @Test
    public void testRechercherTousParPageTrisInvalides() throws Exception {

        /* ARRANGE :
         * prépare une requête paginée avec un TriSpec invalide,
         * puis configure le DAO mocké avec Mockito
         * pour retourner une page nominale.
         * 
         * Ne pas utiliser requete.getTris().add(...),
         * car getTris() retourne une copie défensive.
         */
    	final List<TriSpec> tris = new ArrayList<TriSpec>();
        tris.add(new TriSpec(null, DirectionTri.ASC));
        
        final int pageSize = 2;
        final RequetePage requete = new RequetePage(0, pageSize, tris);
        

        final TypeProduitJPA parentJPA =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        final List<SousTypeProduitJPA> contenu =
                new ArrayList<SousTypeProduitJPA>();
        contenu.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_1, parentJPA));

        final Page<SousTypeProduitJPA> page =
                new PageImpl<SousTypeProduitJPA>(
                        contenu,
                        PageRequest.of(PAGE_0, SIZE_5),
                        TOTAL_10);

        /* Condition du Mock typeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findAll(Pageable) sur le DAO mocké 
         * retourne une Page.
         */
        when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        /* ACT :
         * appelle this.service.rechercherTousParPage(requete)
         * avec un tri invalide dans la requête.
         */
        final ResultatPage<SousTypeProduit> resultat =
                this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * vérifie que le service ne tombe pas en échec
         * et retourne bien la page nominale du DAO.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().hasSize(1);
        assertThat(resultat.getContent().get(0).getSousTypeProduit())
            .isEqualTo(LIBELLE_ENFANT_1);
        assertThat(resultat.getPageNumber()).isEqualTo(PAGE_0);
        assertThat(resultat.getPageSize()).isEqualTo(SIZE_5);
        assertThat(resultat.getTotalElements()).isEqualTo(TOTAL_10);

        /* Vérifie que la méthode findAll(Pageable)
         * du DAO sousTypeProduitDaoJPA mocké avec Mockito 
         * a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).findAll(any(Pageable.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que rechercherTousParPage(page vide) :</p>
     * <ul>
     * <li>retourne un {@link ResultatPage} non null ;</li>
     * <li>retourne un contenu vide ;</li>
     * <li>retourne des métadonnées cohérentes ;</li>
     * <li>appelle une seule fois la méthode findAll(Pageable)
     * du DAO mocké avec Mockito.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(page vide) - retourne une page vide cohérente")
    @Test
    public void testRechercherTousParPagePageVide() throws Exception {

        /* ARRANGE :
         * configure le DAO mocké avec Mockito
         * pour que la méthode findAll(Pageable)
         * retourne une page vide.
         */
        final List<SousTypeProduitJPA> contenu =
                new ArrayList<SousTypeProduitJPA>();

        final Page<SousTypeProduitJPA> page =
                new PageImpl<SousTypeProduitJPA>(
                        contenu,
                        PageRequest.of(PAGE_0, SIZE_5),
                        0L);

        /* Condition du Mock typeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findAll(Pageable) sur le DAO mocké 
         * retourne une Page.
         */
        when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        /* ACT :
         * appelle this.service.rechercherTousParPage(new RequetePage())
         * dans le scénario où la page DAO est vide.
         */
        final ResultatPage<SousTypeProduit> resultat =
                this.service.rechercherTousParPage(new RequetePage());

        /* ASSERT :
         * vérifie que la méthode retourne
         * une page vide, mais exploitable.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().isEmpty();
        assertThat(resultat.getPageNumber()).isEqualTo(PAGE_0);
        assertThat(resultat.getPageSize()).isEqualTo(SIZE_5);
        assertThat(resultat.getTotalElements()).isZero();

        /* Vérifie que la méthode findAll(Pageable)
         * du DAO sousTypeProduitDaoJPA mocké avec Mockito 
         * a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).findAll(any(Pageable.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que rechercherTousParPage(findAll(Pageable) retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle une seule fois la méthode findAll(Pageable)
     * du DAO mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(findAll(Pageable) retourne null) - jette ExceptionTechniqueGateway")
    @Test
    public void testRechercherTousParPagePageNull() {

        /* ARRANGE :
         * configure le DAO mocké avec Mockito
         * pour que la méthode findAll(Pageable) retourne null.
         */
        when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que :
         * this.service.rechercherTousParPage(new RequetePage())
         * avec DAO.findAll(Pageable) retourne null
         * - jette une ExceptionTechniqueGateway
         * - avec un message MSG_ERREUR_TECH_KO_STOCKAGE.
         */
        assertThatThrownBy(() -> this.service.rechercherTousParPage(new RequetePage()))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Vérifie que la méthode findAll(Pageable)
         * du DAO sousTypeProduitDaoJPA mocké avec Mockito 
         * a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).findAll(any(Pageable.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que rechercherTousParPage(contenu page null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle une seule fois la méthode findAll(Pageable)
     * du DAO mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(contenu page null) - jette ExceptionTechniqueGateway")
    @Test
    public void testRechercherTousParPageContenuNull() {

        /* ARRANGE :
         * configure le DAO mocké avec Mockito
         * pour que la méthode findAll(Pageable)
         * retourne une Page dont getContent() retourne null.
         */
        final Page<SousTypeProduitJPA> pageMock = org.mockito.Mockito.mock(Page.class);
        when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(pageMock);
        when(pageMock.getContent()).thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que :
         * this.service.rechercherTousParPage(new RequetePage())
         * avec page.getContent() retourne null
         * jette une ExceptionTechniqueGateway
         * avec un message MSG_ERREUR_TECH_KO_STOCKAGE.
         */
        assertThatThrownBy(() -> this.service.rechercherTousParPage(new RequetePage()))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Vérifie que la méthode findAll(Pageable)
         * du DAO sousTypeProduitDaoJPA mocké avec Mockito 
         * a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).findAll(any(Pageable.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que rechercherTousParPage(KO DAO message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle une seule fois la méthode findAll(Pageable)
     * du DAO mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(KO DAO message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testRechercherTousParPageDaoExceptionMessageNonNull() {

        /* ARRANGE :
         * configure le DAO mocké avec Mockito
         * pour que la méthode findAll(Pageable)
         * jette une RuntimeException avec message non null.
         */
        final RuntimeException ex = new RuntimeException(LIBELLE_ENFANT_1);
        when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.rechercherTousParPage(RequetePage)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.rechercherTousParPage(RequetePage), 
         * - intercepte toute exception éventuellement levée, 
         * - puis stocke cette exception dans la variable throwable 
         * de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.rechercherTousParPage(new RequetePage()));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable.getCause()).isSameAs(ex);

        /* Vérifie que la méthode findAll(Pageable)
         * du DAO sousTypeProduitDaoJPA mocké avec Mockito 
         * a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).findAll(any(Pageable.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que rechercherTousParPage(KO DAO message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle une seule fois la méthode findAll(Pageable)
     * du DAO mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(KO DAO message null) - jette ExceptionTechniqueGateway")
    @Test
    public void testRechercherTousParPageDaoExceptionMessageNull() {

        /* ARRANGE :
         * configure le DAO mocké avec Mockito
         * pour que la méthode findAll(Pageable)
         * jette une RuntimeException sans message.
         */
        final RuntimeException ex = new RuntimeException((String) null);
        when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.rechercherTousParPage(RequetePage)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.rechercherTousParPage(RequetePage), 
         * - intercepte toute exception éventuellement levée, 
         * - puis stocke cette exception dans la variable throwable 
         * de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.rechercherTousParPage(new RequetePage()));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable.getCause()).isSameAs(ex);

        /* Vérifie que la méthode findAll(Pageable)
         * du DAO sousTypeProduitDaoJPA mocké avec Mockito 
         * a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).findAll(any(Pageable.class));

    } // __________________________________________________________________
    

    
    // ======================== findByObjetMetier =========================
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL} ;</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant.</li>
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
         * avec le message MSG_FINDBYOBJETMETIER_KO_PARAM_NULL
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_PARAM_NULL);

        /* 
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que : 
         * - typeProduitDaoJPA.findById(...) n'a jamais été appelé.
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).findAllByTypeProduit(any(TypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK} ;</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(blank) - jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testFindByObjetMetierLibelleBlank() {

        /* ARRANGE :
         * prépare un sous-type dont le libellé est blank,
         * afin de vérifier le contrôle applicatif
         * effectué avant toute recherche réelle.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(BLANK, null, parent);

        /* ACT - ASSERT :
         * vérifie que l'appel avec un libellé blank
         * jette une ExceptionAppliLibelleBlank
         * avec le message MSG_FINDBYOBJETMETIER_KO_LIBELLE_BLANK
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);

        /* 
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que : 
         * - typeProduitDaoJPA.findById(...) n'a jamais été appelé.
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).findAllByTypeProduit(any(TypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParentNull} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL} ;</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(parent null) - jette ExceptionAppliParentNull (contrat du port)")
    @Test
    public void testFindByObjetMetierParentNull() {

        /* ARRANGE :
         * prépare un sous-type sans parent,
         * afin de vérifier le contrôle applicatif
         * imposé par le contrat du port.
         */
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, null);

        /* ACT - ASSERT :
         * vérifie que l'appel avec un parent null
         * jette une ExceptionAppliParentNull
         * avec le message MSG_FINDBYOBJETMETIER_KO_PARENT_NULL
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_PARENT_NULL);

        /* 
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que : 
         * - typeProduitDaoJPA.findById(...) n'a jamais été appelé.
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).findAllByTypeProduit(any(TypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testFindByObjetMetierParentLibelleBlank() {

        /* ARRANGE :
         * prépare un parent dont le libellé est blank,
         * afin de vérifier le contrôle applicatif
         * effectué avant toute recherche réelle du parent.
         */
        final TypeProduit parent = fabriquerTypeProduit(BLANK, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        /* ACT - ASSERT :
         * vérifie que l'appel avec 
         * un objet dont le parent a un libellé parent blank
         * jette une ExceptionAppliLibelleBlank
         * avec le message MSG_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK);

        /* 
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que : 
         * - typeProduitDaoJPA.findById(...) n'a jamais été appelé.
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).findAllByTypeProduit(any(TypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent ;</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(parent ID null) - jette ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testFindByObjetMetierParentIdNull() {

        /* ARRANGE :
         * prépare un parent non persistant
         * dont l'identifiant est null,
         * afin de vérifier le contrôle de persistance
         * effectué avant toute recherche DAO.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, null);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        /* ACT - ASSERT :
         * vérifie que l'appel this.service.findByObjetMetier(...)
         * avec un objet dont le parent n'est pas persistant (id null)
         * jette une ExceptionTechniqueGatewayNonPersistent
         * avec un message contenant MSG_FINDBYOBJETMETIER_PREFIX_PARENT_NON_PERSISTENT
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
         * - typeProduitDaoJPA.findById(...) n'a jamais été appelé.
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).findAllByTypeProduit(any(TypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(parent absent DAO) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent ;</li>
     * <li>appelle la méthode findById(...) du DAO parent mocké avec Mockito ;</li>
     * <li>n'appelle pas la méthode findAllByTypeProduit(...) du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(parent absent DAO) - jette ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testFindByObjetMetierParentAbsent() {

        /* ARRANGE :
         * prépare un parent persistant en apparence,
         * mais absent du DAO parent mocké avec Mockito.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.empty());

        /* ACT - ASSERT :
         * vérifie que l'appel this.service.findByObjetMetier(...)
         * avec un objet dont le parent n'est pas trouvé par le DAO
         * jette une ExceptionTechniqueGatewayNonPersistent
         * avec un message contenant MSG_FINDBYOBJETMETIER_PREFIX_PARENT_NON_PERSISTENT
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
         * - typeProduitDaoJPA.findById(...) a été appelé.
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, never()).findAllByTypeProduit(any(TypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(KO DAO parent message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle la méthode findById(...) du DAO parent mocké avec Mockito ;</li>
     * <li>n'appelle pas la méthode findAllByTypeProduit(...) du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(KO DAO parent message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindByObjetMetierParentDaoExceptionMessageNonNull() {

        /* ARRANGE :
         * configure le DAO parent mocké avec Mockito
         * pour que la méthode findById(ID_1)
         * jette une RuntimeException avec message non null.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final RuntimeException ex = new RuntimeException(LIBELLE_PARENT_1);
        when(this.typeProduitDaoJPA.findById(ID_1)).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.findByObjetMetier(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.findByObjetMetier(...), 
         * - intercepte toute exception éventuellement levée, 
         * - puis stocke cette exception dans la variable throwable 
         * de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.findByObjetMetier(stp));

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
         * - typeProduitDaoJPA.findById(...) a été appelé.
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, never()).findAllByTypeProduit(any(TypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(KO DAO parent message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle la méthode findById(...) du DAO parent mocké avec Mockito ;</li>
     * <li>n'appelle pas la méthode findAllByTypeProduit(...) du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(KO DAO parent message null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindByObjetMetierParentDaoExceptionMessageNull() {

        /* ARRANGE :
         * configure le DAO parent mocké avec Mockito
         * pour que la méthode findById(ID_1)
         * jette une RuntimeException sans message.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final RuntimeException ex = new RuntimeException((String) null);
        when(this.typeProduitDaoJPA.findById(ID_1)).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.findByObjetMetier(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.findByObjetMetier(...), 
         * - intercepte toute exception éventuellement levée, 
         * - puis stocke cette exception dans la variable throwable 
         * de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.findByObjetMetier(stp));

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
         * - typeProduitDaoJPA.findById(...) a été appelé.
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, never()).findAllByTypeProduit(any(TypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(findAllByTypeProduit retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle la méthode findById(...) du DAO parent mocké avec Mockito ;</li>
     * <li>appelle la méthode findAllByTypeProduit(...) du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(findAllByTypeProduit retourne null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindByObjetMetierDaoRetourneNull() {

        /* ARRANGE :
         * configure le DAO parent mocké avec Mockito
         * pour que findById(ID_1) retourne le parent,
         * puis configure le DAO enfant mocké avec Mockito
         * pour que findAllByTypeProduit(parentJPA) retourne null.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        
        /* 
         * Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(...) 
         * sur le DAO mocké 
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));
        
        /* 
         * Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findAllByTypeProduit(...) 
         * sur le DAO mocké 
         * retourne null.
         */
        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que :
         * this.service.findByObjetMetier(stp)
         * avec DAO.findAllByTypeProduit(parentJPA) retourne null
         * jette une ExceptionTechniqueGateway
         * avec un message MSG_ERREUR_TECH_KO_STOCKAGE.
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(stp))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* 
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que : 
         * - typeProduitDaoJPA.findById(...) a été appelé.
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(...) a été appelé.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).findAllByTypeProduit(parentJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(KO DAO enfant message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle la méthode findById(...) du DAO parent mocké avec Mockito ;</li>
     * <li>appelle la méthode findAllByTypeProduit(...) du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(KO DAO enfant message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindByObjetMetierDaoExceptionMessageNonNull() {

        /* ARRANGE :
         * configure le DAO parent mocké avec Mockito
         * pour que findById(ID_1) retourne le parent,
         * puis configure le DAO enfant mocké avec Mockito
         * pour que la méthode findAllByTypeProduit(parentJPA)
         * jette une RuntimeException avec message non null.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        
        /* 
         * Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(...) 
         * sur le DAO mocké 
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final RuntimeException ex = new RuntimeException(LIBELLE_ENFANT_1);
        
        /* 
         * Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findAllByTypeProduit(...) 
         * sur le DAO mocké 
         * jette une Exception ex.
         */
        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.findByObjetMetier(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.findByObjetMetier(...), 
         * - intercepte toute exception éventuellement levée, 
         * - puis stocke cette exception dans la variable throwable 
         * de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.findByObjetMetier(stp));

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
         * - typeProduitDaoJPA.findById(...) a été appelé.
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(...) a été appelé.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).findAllByTypeProduit(parentJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(KO DAO enfant message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle la méthode findById(...) du DAO parent mocké avec Mockito ;</li>
     * <li>appelle la méthode findAllByTypeProduit(...) du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(KO DAO enfant message null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindByObjetMetierDaoExceptionMessageNull() {

        /* ARRANGE :
         * configure le DAO parent mocké avec Mockito
         * pour que findById(ID_1) retourne le parent,
         * puis configure le DAO enfant mocké avec Mockito
         * pour que la méthode findAllByTypeProduit(parentJPA)
         * jette une RuntimeException sans message.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        
        /* 
         * Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(...) 
         * sur le DAO mocké 
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final RuntimeException ex = new RuntimeException((String) null);
        
        /* 
         * Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findAllByTypeProduit(...) 
         * sur le DAO mocké 
         * jette une Exception ex.
         */
        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.findByObjetMetier(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.findByObjetMetier(...), 
         * - intercepte toute exception éventuellement levée, 
         * - puis stocke cette exception dans la variable throwable 
         * de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.findByObjetMetier(stp));

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
         * - typeProduitDaoJPA.findById(...) a été appelé.
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(...) a été appelé.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).findAllByTypeProduit(parentJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(pas trouvé) :</p>
     * <ul>
     * <li>retourne {@code null} ;</li>
     * <li>appelle la méthode findById(...) du DAO parent mocké avec Mockito ;</li>
     * <li>appelle la méthode findAllByTypeProduit(...) du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(pas trouvé) - retourne null")
    @Test
    public void testFindByObjetMetierPasTrouve() throws Exception {

        /* ARRANGE :
         * configure le DAO parent mocké avec Mockito
         * pour que findById(ID_1) retourne le parent,
         * puis configure le DAO enfant mocké avec Mockito
         * pour que findAllByTypeProduit(parentJPA)
         * retourne une liste ne contenant pas le libellé recherché.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_3, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        
        /* 
         * Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(...) 
         * sur le DAO mocké 
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final List<SousTypeProduitJPA> entities = new ArrayList<SousTypeProduitJPA>();
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA));
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_2, ID_3, parentJPA));
        
        /* 
         * Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findAllByTypeProduit(...) 
         * sur le DAO mocké 
         * retourne une liste entities.
         */
        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenReturn(entities);

        /* ACT :
         * appelle this.service.findByObjetMetier(stp)
         * dans le scénario où aucun élément ne correspond.
         */
        final SousTypeProduit retour = this.service.findByObjetMetier(stp);

        /* ASSERT :
         * vérifie que la méthode retourne null
         * lorsque le libellé recherché n'est pas trouvé.
         */
        assertThat(retour).isNull();

        /* 
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que : 
         * - typeProduitDaoJPA.findById(...) a été appelé.
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(...) a été appelé.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).findAllByTypeProduit(parentJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(trouvé) :</p>
     * <ul>
     * <li>retourne un objet métier non null ;</li>
     * <li>retourne le libellé enfant attendu ;</li>
     * <li>retourne un parent non null ;</li>
     * <li>retourne le libellé parent attendu ;</li>
     * <li>appelle la méthode findById(...) du DAO parent mocké avec Mockito ;</li>
     * <li>appelle la méthode findAllByTypeProduit(...) du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(trouvé) - retourne un objet métier cohérent")
    @Test
    public void testFindByObjetMetierTrouve() throws Exception {

        /* ARRANGE :
         * configure le DAO parent mocké avec Mockito
         * pour que findById(ID_1) retourne le parent,
         * puis configure le DAO enfant mocké avec Mockito
         * pour que findAllByTypeProduit(parentJPA)
         * retourne une liste contenant un null
         * et l'élément recherché.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_2, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        
        /* 
         * Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(...) 
         * sur le DAO mocké 
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final List<SousTypeProduitJPA> entities = new ArrayList<SousTypeProduitJPA>();
        entities.add(null);
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA));
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_2, ID_3, parentJPA));
        
        /* 
         * Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findAllByTypeProduit(...) 
         * sur le DAO mocké 
         * retourne une liste entities.
         */
        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenReturn(entities);

        /* ACT :
         * appelle this.service.findByObjetMetier(stp)
         * dans le scénario où l'élément recherché est présent.
         */
        final SousTypeProduit retour = this.service.findByObjetMetier(stp);

        /* ASSERT :
         * vérifie que la méthode retourne bien
         * un objet métier cohérent avec les données mockées.
         */
        assertThat(retour).isNotNull();
        assertThat(retour.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_2);
        assertThat(retour.getTypeProduit()).isNotNull();
        assertThat(retour.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_1);

        /* 
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que : 
         * - typeProduitDaoJPA.findById(...) a été appelé.
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(...) a été appelé.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).findAllByTypeProduit(parentJPA);

    } // __________________________________________________________________    
    

    
    // ========================== findByLibelle ===========================



    /**
     * <div>
     * <p>garantit que findByLibelle(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK} ;</li>
     * <li>n'appelle pas la méthode findBySousTypeProduitIgnoreCase(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYLIBELLE)
    @DisplayName("findByLibelle(null) - jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testFindByLibelleNull() {

        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel service.findByLibelle(...)
         * avec un libellé null
         * jette une ExceptionAppliLibelleBlank
         * avec le message MSG_FINDBYLIBELLE_KO_LIBELLE_BLANK
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByLibelle(null))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYLIBELLE_KO_LIBELLE_BLANK);

        /* 
         * Vérifie ensuite que DAO.findBySousTypeProduitIgnoreCase(...)
         * n'a jamais été appelé.
         */
        verify(this.sousTypeProduitDaoJPA, never())
            .findBySousTypeProduitIgnoreCase(any());

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelle(blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK} ;</li>
     * <li>n'appelle pas la méthode findBySousTypeProduitIgnoreCase(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYLIBELLE)
    @DisplayName("findByLibelle(blank) - jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testFindByLibelleBlank() {

        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel service.findByLibelle(...)
         * avec un libellé blank
         * jette une ExceptionAppliLibelleBlank
         * avec le message MSG_FINDBYLIBELLE_KO_LIBELLE_BLANK
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByLibelle(BLANK))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDBYLIBELLE_KO_LIBELLE_BLANK);

        /* 
         * Vérifie ensuite que DAO.findBySousTypeProduitIgnoreCase(...)
         * n'a jamais été appelé.
         */
        verify(this.sousTypeProduitDaoJPA, never())
            .findBySousTypeProduitIgnoreCase(any());

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelle(findBySousTypeProduitIgnoreCase retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle une seule fois la méthode findBySousTypeProduitIgnoreCase(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYLIBELLE)
    @DisplayName("findByLibelle(findBySousTypeProduitIgnoreCase retourne null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindByLibelleDaoRetourneNull() {

        /* ARRANGE :
         * configure le DAO enfant mocké avec Mockito
         * pour que la méthode findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1)
         * retourne null.
         */
        when(this.sousTypeProduitDaoJPA.findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1))
            .thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que :
         * this.service.findByLibelle(LIBELLE_ENFANT_1)
         * avec DAO.findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1) retourne null
         * jette une ExceptionTechniqueGateway
         * avec un message MSG_ERREUR_TECH_KO_STOCKAGE.
         */
        assertThatThrownBy(() -> this.service.findByLibelle(LIBELLE_ENFANT_1))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Vérifie que la méthode findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1)
         * du DAO enfant mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA)
            .findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelle(KO DAO message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle une seule fois la méthode findBySousTypeProduitIgnoreCase(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYLIBELLE)
    @DisplayName("findByLibelle(KO DAO message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindByLibelleDaoExceptionMessageNonNull() {

        /* ARRANGE :
         * configure le DAO enfant mocké avec Mockito
         * pour que la méthode findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1)
         * jette une RuntimeException avec message non null.
         */
        final RuntimeException ex = new RuntimeException(LIBELLE_ENFANT_1);
        when(this.sousTypeProduitDaoJPA.findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1))
            .thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.findByLibelle(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.findByLibelle(...), 
         * - intercepte toute exception éventuellement levée, 
         * - puis stocke cette exception dans la variable throwable 
         * de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.findByLibelle(LIBELLE_ENFANT_1));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable.getCause()).isSameAs(ex);

        /* Vérifie que la méthode findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1)
         * du DAO enfant mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA)
            .findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelle(KO DAO message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle une seule fois la méthode findBySousTypeProduitIgnoreCase(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYLIBELLE)
    @DisplayName("findByLibelle(KO DAO message null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindByLibelleDaoExceptionMessageNull() {

        /* ARRANGE :
         * configure le DAO enfant mocké avec Mockito
         * pour que la méthode findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1)
         * jette une RuntimeException sans message.
         */
        final RuntimeException ex = new RuntimeException((String) null);
        when(this.sousTypeProduitDaoJPA.findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1))
            .thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.findByLibelle(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.findByLibelle(...), 
         * - intercepte toute exception éventuellement levée, 
         * - puis stocke cette exception dans la variable throwable 
         * de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.findByLibelle(LIBELLE_ENFANT_1));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable.getCause()).isSameAs(ex);

        /* Vérifie que la méthode findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1)
         * du DAO enfant mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA)
            .findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelle(vide) :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>retourne une liste vide ;</li>
     * <li>appelle une seule fois la méthode findBySousTypeProduitIgnoreCase(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYLIBELLE)
    @DisplayName("findByLibelle(vide) - retourne une liste vide")
    @Test
    public void testFindByLibelleVide() throws Exception {

        /* ARRANGE :
         * configure le DAO enfant mocké avec Mockito
         * pour que la méthode findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1)
         * retourne une liste vide.
         */
        when(this.sousTypeProduitDaoJPA.findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1))
            .thenReturn(new ArrayList<SousTypeProduitJPA>());

        /* ACT :
         * appelle this.service.findByLibelle(LIBELLE_ENFANT_1)
         * dans le scénario où le DAO retourne une liste vide.
         */
        final List<SousTypeProduit> retour =
                this.service.findByLibelle(LIBELLE_ENFANT_1);

        /* ASSERT :
         * vérifie que la méthode retourne bien
         * une liste non nulle, mais vide.
         */
        assertThat(retour).isNotNull().isEmpty();

        /* Vérifie que la méthode findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1)
         * du DAO enfant mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA)
            .findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelle(nominal) :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>retourne le libellé enfant attendu ;</li>
     * <li>retourne un parent non null ;</li>
     * <li>retourne le libellé parent attendu ;</li>
     * <li>appelle une seule fois la méthode findBySousTypeProduitIgnoreCase(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYLIBELLE)
    @DisplayName("findByLibelle(nominal) - retourne une liste métier cohérente")
    @Test
    public void testFindByLibelleNominal() throws Exception {

        /* ARRANGE :
         * configure le DAO enfant mocké avec Mockito
         * pour que la méthode findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1)
         * retourne une liste contenant un élément nominal.
         */
        final TypeProduitJPA parentJPA =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        final List<SousTypeProduitJPA> entities =
                new ArrayList<SousTypeProduitJPA>();
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_1, parentJPA));

        /* 
         * Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findBySousTypeProduitIgnoreCase(...) 
         * sur le DAO mocké 
         * retourne une liste entities.
         */
        when(this.sousTypeProduitDaoJPA.findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1))
            .thenReturn(entities);

        /* ACT :
         * appelle this.service.findByLibelle(LIBELLE_ENFANT_1)
         * dans le scénario nominal.
         */
        final List<SousTypeProduit> retour =
                this.service.findByLibelle(LIBELLE_ENFANT_1);

        /* ASSERT :
         * vérifie que la méthode retourne bien
         * une liste métier cohérente avec les données mockées.
         */
        assertThat(retour).isNotNull().hasSize(1);
        assertThat(retour.get(0).getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_1);
        assertThat(retour.get(0).getTypeProduit()).isNotNull();
        assertThat(retour.get(0).getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_1);

        /* Vérifie que la méthode findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1)
         * du DAO enfant mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA)
            .findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1);

    } // __________________________________________________________________
    
    

    // ======================== findByLibelleRapide =======================
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL} ;</li>
     * <li>n'appelle ni la méthode findAll()
     * ni la méthode findBySousTypeProduitContainingIgnoreCase(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYLIBELLERAPIDE)
    @DisplayName("findByLibelleRapide(null) - jette ExceptionAppliParamNull (contrat du port)")
    @Test
    public void testFindByLibelleRapideNull() {

        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel this.service.findByLibelleRapide(...)
         * avec un paramètre null
         * jette une ExceptionAppliParamNull
         * avec le message MSG_FINDBYLIBELLERAPIDE_KO_PARAM_NULL
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.findByLibelleRapide(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_FINDBYLIBELLERAPIDE_KO_PARAM_NULL);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * - sousTypeProduitDaoJPA.findAll() n'a jamais été appelé.
         * - sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase(...)
         *   n'a jamais été appelé.
         */
        verify(this.sousTypeProduitDaoJPA, never()).findAll();
        verify(this.sousTypeProduitDaoJPA, never())
            .findBySousTypeProduitContainingIgnoreCase(any());

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(blank mais pas null) :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>retourne le résultat de rechercherTous() ;</li>
     * <li>appelle la méthode findAll() du DAO enfant mocké avec Mockito ;</li>
     * <li>n'appelle pas la méthode findBySousTypeProduitContainingIgnoreCase(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     * @throws Exception 
     */
    @Tag(TAG_FINDBYLIBELLERAPIDE)
    @DisplayName("findByLibelleRapide(blank mais pas null) - retourne rechercherTous()")
    @Test
    public void testFindByLibelleRapideBlankRetourneTous() throws Exception {

        /* ARRANGE :
         * vérifie d'abord que BLANK est bien blank mais pas null,
         * puis configure le DAO mocké avec Mockito
         * pour que sousTypeProduitDaoJPA.findAll() retourne une liste vide.
         */
        assertThat(isBlankButNotNull(BLANK)).isTrue();

        when(this.sousTypeProduitDaoJPA.findAll())
            .thenReturn(new ArrayList<SousTypeProduitJPA>());

        /* ACT :
         * appelle this.service.findByLibelleRapide(BLANK)
         * dans le scénario où le contenu est blank mais pas null.
         */
        final List<SousTypeProduit> retour =
                this.service.findByLibelleRapide(BLANK);

        /* ASSERT :
         * vérifie que la méthode retourne bien
         * une liste non nulle, mais vide,
         * conformément au comportement de rechercherTous().
         */
        assertThat(retour).isNotNull().isEmpty();

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - sousTypeProduitDaoJPA.findAll() a été appelée une fois.
         * - sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase(...)
         *   n'a jamais été appelée.
         */
        verify(this.sousTypeProduitDaoJPA).findAll();
        verify(this.sousTypeProduitDaoJPA, never())
            .findBySousTypeProduitContainingIgnoreCase(any());

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(DAO retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle une seule fois la méthode
     * findBySousTypeProduitContainingIgnoreCase(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYLIBELLERAPIDE)
    @DisplayName("findByLibelleRapide(DAO retourne null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindByLibelleRapideDaoRetourneNull() {

        /* ARRANGE :
         * configure le DAO enfant mocké avec Mockito
         * pour que sousTypeProduitDaoJPA
         * .findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL)
         * retourne null.
         */
        when(this.sousTypeProduitDaoJPA
                .findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL))
            .thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que :
         * this.service.findByLibelleRapide(CONTENU_PARTIEL)
         * avec sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase(...)
         * qui retourne null
         * - jette une ExceptionTechniqueGateway
         * - avec un message MSG_ERREUR_TECH_KO_STOCKAGE
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.findByLibelleRapide(CONTENU_PARTIEL))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /*
         * Vérifie que la méthode
         * sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL)
         * du DAO mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA)
            .findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(KO DAO message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle une seule fois la méthode
     * findBySousTypeProduitContainingIgnoreCase(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYLIBELLERAPIDE)
    @DisplayName("findByLibelleRapide(KO DAO message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindByLibelleRapideDaoExceptionMessageNonNull() {

        /* ARRANGE :
         * configure le DAO enfant mocké avec Mockito
         * pour que la méthode
         * sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL)
         * jette une RuntimeException avec message non null.
         */
        final RuntimeException ex = new RuntimeException(CONTENU_PARTIEL);

        when(this.sousTypeProduitDaoJPA
                .findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL))
            .thenThrow(ex);

        /* ACT :
         * sollicite la méthode this.service.findByLibelleRapide(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.findByLibelleRapide(...)
         * - intercepte toute exception éventuellement levée
         * - puis stocke cette exception dans la variable throwable
         *   de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.findByLibelleRapide(CONTENU_PARTIEL));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable.getCause()).isSameAs(ex);

        /*
         * Vérifie que la méthode
         * sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL)
         * du DAO mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA)
            .findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(KO DAO message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle une seule fois la méthode
     * findBySousTypeProduitContainingIgnoreCase(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYLIBELLERAPIDE)
    @DisplayName("findByLibelleRapide(KO DAO message null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindByLibelleRapideDaoExceptionMessageNull() {

        /* ARRANGE :
         * configure le DAO enfant mocké avec Mockito
         * pour que la méthode
         * sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL)
         * jette une RuntimeException sans message.
         */
        final RuntimeException ex = new RuntimeException((String) null);

        when(this.sousTypeProduitDaoJPA
                .findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL))
            .thenThrow(ex);

        /* ACT :
         * sollicite la méthode this.service.findByLibelleRapide(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.findByLibelleRapide(...)
         * - intercepte toute exception éventuellement levée
         * - puis stocke cette exception dans la variable throwable
         *   de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(
                        () -> this.service.findByLibelleRapide(CONTENU_PARTIEL));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable.getCause()).isSameAs(ex);

        /*
         * Vérifie que la méthode
         * sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL)
         * du DAO mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA)
            .findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(contenu partiel avec caractères spéciaux) :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>retourne le libellé enfant attendu ;</li>
     * <li>retourne un parent non null ;</li>
     * <li>retourne le libellé parent attendu ;</li>
     * <li>appelle une seule fois la méthode
     * findBySousTypeProduitContainingIgnoreCase(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYLIBELLERAPIDE)
    @DisplayName("findByLibelleRapide(contenu partiel avec caractères spéciaux) - retourne une liste métier cohérente")
    @Test
    public void testFindByLibelleRapideContenuSpeciauxNominal() throws Exception {

        /* ARRANGE :
         * configure le DAO enfant mocké avec Mockito
         * pour que la méthode
         * sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase(\"prod/uit\")
         * retourne une liste contenant un élément nominal.
         */
        final TypeProduitJPA parentJPA =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        final List<SousTypeProduitJPA> entities =
                new ArrayList<SousTypeProduitJPA>();
        entities.add(fabriquerSousTypeProduitJPA("prod/uit_1", ID_1, parentJPA));

        /*
         * Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase("prod/uit")
         * sur le DAO mocké
         * retourne une liste entities.
         */
        when(this.sousTypeProduitDaoJPA
                .findBySousTypeProduitContainingIgnoreCase("prod/uit"))
            .thenReturn(entities);

        /* ACT :
         * appelle this.service.findByLibelleRapide("prod/uit")
         * dans le scénario nominal avec caractères spéciaux.
         */
        final List<SousTypeProduit> retour =
                this.service.findByLibelleRapide("prod/uit");

        /* ASSERT :
         * vérifie que la méthode retourne bien
         * une liste métier cohérente avec les données mockées.
         */
        assertThat(retour).isNotNull().hasSize(1);
        assertThat(retour.get(0).getSousTypeProduit()).isEqualTo("prod/uit_1");
        assertThat(retour.get(0).getTypeProduit()).isNotNull();
        assertThat(retour.get(0).getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_1);

        /* Vérifie que la méthode
         * sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase("prod/uit")
         * du DAO mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA)
            .findBySousTypeProduitContainingIgnoreCase("prod/uit");

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelleRapide(nominal) :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>retourne le libellé enfant attendu ;</li>
     * <li>retourne un parent non null ;</li>
     * <li>retourne le libellé parent attendu ;</li>
     * <li>appelle une seule fois la méthode
     * findBySousTypeProduitContainingIgnoreCase(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYLIBELLERAPIDE)
    @DisplayName("findByLibelleRapide(nominal) - retourne une liste métier cohérente")
    @Test
    public void testFindByLibelleRapideNominal() throws Exception {

        /* ARRANGE :
         * configure le DAO enfant mocké avec Mockito
         * pour que la méthode
         * sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL)
         * retourne une liste contenant un élément nominal.
         */
        final TypeProduitJPA parentJPA =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        final List<SousTypeProduitJPA> entities =
                new ArrayList<SousTypeProduitJPA>();
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_1, parentJPA));

        /*
         * Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL)
         * sur le DAO mocké
         * retourne une liste entities.
         */
        when(this.sousTypeProduitDaoJPA
                .findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL))
            .thenReturn(entities);

        /* ACT :
         * appelle this.service.findByLibelleRapide(CONTENU_PARTIEL)
         * dans le scénario nominal.
         */
        final List<SousTypeProduit> retour =
                this.service.findByLibelleRapide(CONTENU_PARTIEL);

        /* ASSERT :
         * vérifie que la méthode retourne bien
         * une liste métier cohérente avec les données mockées.
         */
        assertThat(retour).isNotNull().hasSize(1);
        assertThat(retour.get(0).getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_1);
        assertThat(retour.get(0).getTypeProduit()).isNotNull();
        assertThat(retour.get(0).getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_1);

        /* Vérifie que la méthode
         * sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL)
         * du DAO mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA)
            .findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL);

    } // __________________________________________________________________
    
    
    
    // ========================= findAllByParent ==========================
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParentNull} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL} ;</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(null) - jette ExceptionAppliParentNull (contrat du port)")
    @Test
    public void testFindAllByParentNull() {

        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel service.findAllByParent(...) avec un paramètre null
         * jette une ExceptionAppliParentNull
         * avec le message MSG_FINDALLBYPARENT_KO_PARAM_NULL
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.findAllByParent(null))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(MSG_FINDALLBYPARENT_KO_PARAM_NULL);

        /* 
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * - typeProduitDaoJPA.findById(...) n'a jamais été appelé.
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never())
            .findAllByTypeProduit(any(TypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(parent libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant.</li>
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
         * vérifie que l'appel service.findAllByParent(...)
         * avec un parent ayant un libellé blank
         * jette une ExceptionAppliLibelleBlank
         * avec le message MSG_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK);

        /* 
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * - typeProduitDaoJPA.findById(...) n'a jamais été appelé.
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never())
            .findAllByTypeProduit(any(TypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(parent non persistant) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent ;</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(parent non persistant) - jette ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testFindAllByParentParentNonPersistant() {

        /* ARRANGE :
         * prépare un parent non persistant
         * dont l'identifiant est null,
         * afin de vérifier le contrôle de persistance
         * effectué avant toute recherche DAO.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, null);

        /* ACT - ASSERT :
         * vérifie que l'appel service.findAllByParent(...)
         * avec un parent non persistant
         * jette une ExceptionTechniqueGatewayNonPersistent
         * avec un message contenant MSG_FINDALLBYPARENT_PREFIX_PARENT_NON_PERSISTENT
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(construireMessageNonPersistent(
                    MSG_FINDALLBYPARENT_PREFIX_PARENT_NON_PERSISTENT, LIBELLE_PARENT_1));

        /* 
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * - typeProduitDaoJPA.findById(...) n'a jamais été appelé.
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never())
            .findAllByTypeProduit(any(TypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(parent absent DAO) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent ;</li>
     * <li>appelle la méthode findById(...) du DAO parent mocké avec Mockito ;</li>
     * <li>n'appelle pas la méthode findAllByTypeProduit(...) du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(parent absent DAO) - jette ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testFindAllByParentParentAbsent() {

        /* ARRANGE :
         * prépare un parent persistant en apparence,
         * mais absent du DAO mocké avec Mockito.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);

        /* Condition du Mock :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké
         * retourne Optional.empty().
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.empty());

        /* ACT - ASSERT :
         * vérifie que :
         * this.service.findAllByParent(parent)
         * avec typeProduitDaoJPA.findById(ID_1) qui retourne Optional.empty()
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - avec un message contenant MSG_FINDALLBYPARENT_PREFIX_PARENT_NON_PERSISTENT
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(construireMessageNonPersistent(
                    MSG_FINDALLBYPARENT_PREFIX_PARENT_NON_PERSISTENT, LIBELLE_PARENT_1));

        /* 
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - typeProduitDaoJPA.findById(ID_1) a été appelé une fois.
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, never())
            .findAllByTypeProduit(any(TypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(KO DAO parent message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle la méthode findById(...) du DAO parent mocké avec Mockito ;</li>
     * <li>n'appelle pas la méthode findAllByTypeProduit(...) du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(KO DAO parent message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindAllByParentParentDaoExceptionMessageNonNull() {

        /* ARRANGE :
         * prépare un parent persistant en apparence,
         * puis configure le DAO parent mocké avec Mockito
         * pour jeter une RuntimeException avec message non null.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final RuntimeException ex = new RuntimeException(LIBELLE_PARENT_1);

        /* Condition du Mock :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké
         * jette l'Exception ex.
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.findAllByParent(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.findAllByParent(...)
         * - intercepte toute exception éventuellement levée
         * - puis stocke cette exception dans la variable throwable de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.findAllByParent(parent));

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
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, never())
            .findAllByTypeProduit(any(TypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(KO DAO parent message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle la méthode findById(...) du DAO parent mocké avec Mockito ;</li>
     * <li>n'appelle pas la méthode findAllByTypeProduit(...) du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(KO DAO parent message null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindAllByParentParentDaoExceptionMessageNull() {

        /* ARRANGE :
         * prépare un parent persistant en apparence,
         * puis configure le DAO parent mocké avec Mockito
         * pour jeter une RuntimeException sans message.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final RuntimeException ex = new RuntimeException((String) null);

        /* Condition du Mock :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké
         * jette l'Exception ex.
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.findAllByParent(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.findAllByParent(...)
         * - intercepte toute exception éventuellement levée
         * - puis stocke cette exception dans la variable throwable de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.findAllByParent(parent));

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
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(...) n'a jamais été appelé.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, never())
            .findAllByTypeProduit(any(TypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(findAllByTypeProduit retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle la méthode findById(...) du DAO parent mocké avec Mockito ;</li>
     * <li>appelle la méthode findAllByTypeProduit(...) du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(findAllByTypeProduit retourne null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindAllByParentDaoRetourneNull() {

        /* ARRANGE :
         * configure le DAO parent mocké avec Mockito
         * pour que typeProduitDaoJPA.findById(ID_1) retourne le parent,
         * puis configure le DAO enfant mocké avec Mockito
         * pour que sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA) retourne null.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA) sur le DAO mocké
         * retourne null.
         */
        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que :
         * this.service.findAllByParent(parent)
         * avec sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA) qui retourne null
         * - jette une ExceptionTechniqueGateway
         * - avec un message MSG_ERREUR_TECH_KO_STOCKAGE
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.findAllByParent(parent))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* 
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - typeProduitDaoJPA.findById(ID_1) a été appelé une fois.
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA) a été appelé une fois.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).findAllByTypeProduit(parentJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(KO DAO enfant message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle la méthode findById(...) du DAO parent mocké avec Mockito ;</li>
     * <li>appelle la méthode findAllByTypeProduit(...) du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(KO DAO enfant message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindAllByParentDaoExceptionMessageNonNull() {

        /* ARRANGE :
         * configure le DAO parent mocké avec Mockito
         * pour que typeProduitDaoJPA.findById(ID_1) retourne le parent,
         * puis configure le DAO enfant mocké avec Mockito
         * pour que la méthode sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)
         * jette une RuntimeException avec message non null.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final RuntimeException ex = new RuntimeException(LIBELLE_PARENT_1);

        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA) sur le DAO mocké
         * jette l'Exception ex.
         */
        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.findAllByParent(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.findAllByParent(...)
         * - intercepte toute exception éventuellement levée
         * - puis stocke cette exception dans la variable throwable de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.findAllByParent(parent));

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
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA) a été appelé une fois.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).findAllByTypeProduit(parentJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(KO DAO enfant message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle la méthode findById(...) du DAO parent mocké avec Mockito ;</li>
     * <li>appelle la méthode findAllByTypeProduit(...) du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(KO DAO enfant message null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindAllByParentDaoExceptionMessageNull() {

        /* ARRANGE :
         * configure le DAO parent mocké avec Mockito
         * pour que typeProduitDaoJPA.findById(ID_1) retourne le parent,
         * puis configure le DAO enfant mocké avec Mockito
         * pour que la méthode sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)
         * jette une RuntimeException sans message.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final RuntimeException ex = new RuntimeException((String) null);

        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA) sur le DAO mocké
         * jette l'Exception ex.
         */
        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.findAllByParent(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.findAllByParent(...)
         * - intercepte toute exception éventuellement levée
         * - puis stocke cette exception dans la variable throwable de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.findAllByParent(parent));

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
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA) a été appelé une fois.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).findAllByTypeProduit(parentJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(vide) :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>retourne une liste vide ;</li>
     * <li>appelle la méthode findById(...) du DAO parent mocké avec Mockito ;</li>
     * <li>appelle la méthode findAllByTypeProduit(...) du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(vide) - retourne une liste vide")
    @Test
    public void testFindAllByParentVide() throws Exception {

        /* ARRANGE :
         * configure le DAO parent mocké avec Mockito
         * pour que typeProduitDaoJPA.findById(ID_1) retourne le parent,
         * puis configure le DAO enfant mocké avec Mockito
         * pour que sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)
         * retourne une liste vide.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA) sur le DAO mocké
         * retourne une liste vide.
         */
        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA))
            .thenReturn(new ArrayList<SousTypeProduitJPA>());

        /* ACT :
         * appelle this.service.findAllByParent(parent)
         * dans le scénario où la liste DAO est vide.
         */
        final List<SousTypeProduit> retour = this.service.findAllByParent(parent);

        /* ASSERT :
         * vérifie que la méthode retourne bien
         * une liste non nulle, mais vide.
         */
        assertThat(retour).isNotNull().isEmpty();

        /* 
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - typeProduitDaoJPA.findById(ID_1) a été appelé une fois.
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA) a été appelé une fois.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).findAllByTypeProduit(parentJPA);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findAllByParent(nominal) :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>filtre les valeurs null ;</li>
     * <li>retourne le libellé enfant attendu ;</li>
     * <li>retourne un parent non null ;</li>
     * <li>retourne le libellé parent attendu ;</li>
     * <li>appelle la méthode findById(...) du DAO parent mocké avec Mockito ;</li>
     * <li>appelle la méthode findAllByTypeProduit(...) du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDALLBYPARENT)
    @DisplayName("findAllByParent(nominal) - retourne une liste métier cohérente")
    @Test
    public void testFindAllByParentNominal() throws Exception {

        /* ARRANGE :
         * configure le DAO parent mocké avec Mockito
         * pour que typeProduitDaoJPA.findById(ID_1) retourne le parent,
         * puis configure le DAO enfant mocké avec Mockito
         * pour que sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)
         * retourne une liste contenant un élément nominal et une valeur null.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final List<SousTypeProduitJPA> entities = new ArrayList<SousTypeProduitJPA>();
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_1, parentJPA));
        entities.add(null);

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA) sur le DAO mocké
         * retourne la liste entities.
         */
        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenReturn(entities);

        /* ACT :
         * appelle this.service.findAllByParent(parent)
         * dans le scénario nominal.
         */
        final List<SousTypeProduit> retour = this.service.findAllByParent(parent);

        /* ASSERT :
         * vérifie que la méthode retourne bien
         * une liste métier cohérente avec les données mockées.
         */
        assertThat(retour).isNotNull().hasSize(1);
        assertThat(retour.get(0).getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_1);
        assertThat(retour.get(0).getTypeProduit()).isNotNull();
        assertThat(retour.get(0).getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_1);

        /* 
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - typeProduitDaoJPA.findById(ID_1) a été appelé une fois.
         * - sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA) a été appelé une fois.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).findAllByTypeProduit(parentJPA);

    } // __________________________________________________________________
    

    
    // ============================ findById ==============================



    /**
     * <div>
     * <p>garantit que findById(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_FINDBYID_KO_PARAM_NULL} ;</li>
     * <li>n'appelle pas la méthode findById(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYID)
    @DisplayName("findById(null) - jette ExceptionAppliParamNull (contrat du port)")
    @Test
    public void testFindByIdNull() {

        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel this.service.findById(...)
         * avec un identifiant null
         * jette une ExceptionAppliParamNull
         * avec le message MSG_FINDBYID_KO_PARAM_NULL
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.findById(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_FINDBYID_KO_PARAM_NULL);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * - sousTypeProduitDaoJPA.findById(...) n'a jamais été appelée.
         */
        verify(this.sousTypeProduitDaoJPA, never()).findById(anyLong());

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findById(findById retourne null Optional) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle une seule fois la méthode findById(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYID)
    @DisplayName("findById(findById retourne null Optional) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindByIdDaoRetourneNullOptional() {

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(ID_1)
         * sur le DAO mocké retourne null.
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_1)).thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que :
         * this.service.findById(ID_1)
         * avec sousTypeProduitDaoJPA.findById(ID_1) qui retourne null
         * - jette une ExceptionTechniqueGateway
         * - avec un message MSG_ERREUR_TECH_KO_STOCKAGE
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.findById(ID_1))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /*
         * Vérifie que la méthode sousTypeProduitDaoJPA.findById(ID_1)
         * du DAO mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).findById(ID_1);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findById(absent) :</p>
     * <ul>
     * <li>retourne {@code null} ;</li>
     * <li>appelle une seule fois la méthode findById(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYID)
    @DisplayName("findById(absent) - retourne null")
    @Test
    public void testFindByIdAbsent() throws Exception {

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(ID_1)
         * sur le DAO mocké retourne Optional.empty().
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.empty());

        /* ACT :
         * appelle this.service.findById(ID_1)
         * dans le scénario où aucun objet n'est trouvé.
         */
        final SousTypeProduit retour = this.service.findById(ID_1);

        /* ASSERT :
         * vérifie que la méthode retourne null
         * lorsque l'identifiant recherché est absent du stockage mocké.
         */
        assertThat(retour).isNull();

        /*
         * Vérifie que la méthode sousTypeProduitDaoJPA.findById(ID_1)
         * du DAO mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).findById(ID_1);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findById(KO DAO message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle une seule fois la méthode findById(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYID)
    @DisplayName("findById(KO DAO message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindByIdDaoExceptionMessageNonNull() {

        /* ARRANGE :
         * configure le DAO enfant mocké avec Mockito
         * pour que la méthode sousTypeProduitDaoJPA.findById(ID_2)
         * jette une RuntimeException avec message non null.
         */
        final RuntimeException ex = new RuntimeException(LIBELLE_ENFANT_1);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(ID_2)
         * sur le DAO mocké jette l'Exception ex.
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenThrow(ex);

        /* ACT :
         * sollicite la méthode this.service.findById(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.findById(...)
         * - intercepte toute exception éventuellement levée
         * - puis stocke cette exception dans la variable throwable
         *   de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.findById(ID_2));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable.getCause()).isSameAs(ex);

        /*
         * Vérifie que la méthode sousTypeProduitDaoJPA.findById(ID_2)
         * du DAO mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).findById(ID_2);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findById(KO DAO message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle une seule fois la méthode findById(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYID)
    @DisplayName("findById(KO DAO message null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindByIdDaoExceptionMessageNull() {

        /* ARRANGE :
         * configure le DAO enfant mocké avec Mockito
         * pour que la méthode sousTypeProduitDaoJPA.findById(ID_2)
         * jette une RuntimeException sans message.
         */
        final RuntimeException ex = new RuntimeException((String) null);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(ID_2)
         * sur le DAO mocké jette l'Exception ex.
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenThrow(ex);

        /* ACT :
         * sollicite la méthode this.service.findById(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.findById(...)
         * - intercepte toute exception éventuellement levée
         * - puis stocke cette exception dans la variable throwable
         *   de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.findById(ID_2));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable.getCause()).isSameAs(ex);

        /*
         * Vérifie que la méthode sousTypeProduitDaoJPA.findById(ID_2)
         * du DAO mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).findById(ID_2);

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findById(nominal) :</p>
     * <ul>
     * <li>retourne un objet métier non null ;</li>
     * <li>retourne le libellé enfant attendu ;</li>
     * <li>retourne un parent non null ;</li>
     * <li>retourne le libellé parent attendu ;</li>
     * <li>appelle une seule fois la méthode findById(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYID)
    @DisplayName("findById(nominal) - retourne un objet métier cohérent")
    @Test
    public void testFindByIdNominal() throws Exception {

        /* ARRANGE :
         * configure le DAO enfant mocké avec Mockito
         * pour que la méthode sousTypeProduitDaoJPA.findById(ID_2)
         * retourne un SousTypeProduitJPA nominal.
         */
        final TypeProduitJPA parentJPA =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduitJPA entityJPA =
                fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(ID_2)
         * sur le DAO mocké retourne Optional.of(entityJPA).
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.of(entityJPA));

        /* ACT :
         * appelle this.service.findById(ID_2)
         * dans le scénario nominal.
         */
        final SousTypeProduit retour = this.service.findById(ID_2);

        /* ASSERT :
         * vérifie que la méthode retourne bien
         * un objet métier cohérent avec les données mockées.
         */
        assertThat(retour).isNotNull();
        assertThat(retour.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_1);
        assertThat(retour.getTypeProduit()).isNotNull();
        assertThat(retour.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_1);

        /*
         * Vérifie que la méthode sousTypeProduitDaoJPA.findById(ID_2)
         * du DAO mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).findById(ID_2);

    } // __________________________________________________________________
    

    
    // ============================= update ===============================
    
    
    
    /**
     * <div>
     * <p>garantit que update(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_PARAM_NULL} ;</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(null) - jette ExceptionAppliParamNull (contrat du port)")
    @Test
    public void testUpdateNull() {

        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel service.update(...) avec un paramètre null
         * jette une ExceptionAppliParamNull
         * avec le message MSG_UPDATE_KO_PARAM_NULL
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.update(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_UPDATE_KO_PARAM_NULL);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * Assure que :
         * - typeProduitDaoJPA.findById(...) n'a jamais été appelée.
         * - sousTypeProduitDaoJPA.findById(...) n'a jamais été appelée.
         * - sousTypeProduitDaoJPA.save(...) n'a jamais été appelée.
         */
        verify(this.typeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_BLANK} ;</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(blank) - jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testUpdateLibelleBlank() {

        /* ARRANGE :
         * prépare un sous-type persistant en apparence
         * dont le libellé est blank,
         * afin de vérifier le contrôle applicatif
         * effectué avant toute tentative d'accès au stockage.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(BLANK, ID_1, parent);

        /* ACT - ASSERT :
         * vérifie que l'appel service.update(...)
         * avec un objet métier ayant un libellé blank
         * jette une ExceptionAppliLibelleBlank
         * avec le message MSG_UPDATE_KO_LIBELLE_BLANK
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_UPDATE_KO_LIBELLE_BLANK);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * Assure que :
         * - typeProduitDaoJPA.findById(...) n'a jamais été appelée.
         * - sousTypeProduitDaoJPA.findById(...) n'a jamais été appelée.
         * - sousTypeProduitDaoJPA.save(...) n'a jamais été appelée.
         */
        verify(this.typeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNonPersistent} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_NON_PERSISTENT}
     * suivi du libellé de l'objet ;</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(ID null) - jette ExceptionAppliParamNonPersistent (contrat du port)")
    @Test
    public void testUpdateIdNull() {

        /* ARRANGE :
         * prépare un sous-type non persistant
         * dont l'identifiant est null,
         * afin de vérifier le contrôle de persistance
         * effectué avant toute recherche DAO.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        /* ACT - ASSERT :
         * vérifie que l'appel service.update(...)
         * avec un objet non persistant
         * jette une ExceptionAppliParamNonPersistent
         * avec un message contenant MSG_UPDATE_PREFIX_NON_PERSISTENT
         * suivi du libellé de l'objet.
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(MSG_UPDATE_PREFIX_NON_PERSISTENT + safeMessage(LIBELLE_ENFANT_1));

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * Assure que :
         * - typeProduitDaoJPA.findById(...) n'a jamais été appelée.
         * - sousTypeProduitDaoJPA.findById(...) n'a jamais été appelée.
         * - sousTypeProduitDaoJPA.save(...) n'a jamais été appelée.
         */
        verify(this.typeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(parent null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParentNull} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_PARENT_NULL} ;</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent null) - jette ExceptionAppliParentNull (contrat du port)")
    @Test
    public void testUpdateParentNull() {

        /* ARRANGE :
         * prépare un sous-type sans parent,
         * afin de vérifier le contrôle applicatif
         * imposé par le contrat du port.
         */
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_1, null);

        /* ACT - ASSERT :
         * vérifie que l'appel service.update(...)
         * avec un objet ayant un parent null
         * jette une ExceptionAppliParentNull
         * avec le message MSG_UPDATE_KO_PARENT_NULL
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionAppliParentNull.class)
            .hasMessage(MSG_UPDATE_KO_PARENT_NULL);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * Assure que :
         * - typeProduitDaoJPA.findById(...) n'a jamais été appelée.
         * - sousTypeProduitDaoJPA.findById(...) n'a jamais été appelée.
         * - sousTypeProduitDaoJPA.save(...) n'a jamais été appelée.
         */
        verify(this.typeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(parent libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK} ;</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent libellé blank) - jette ExceptionAppliLibelleBlank (contrat du port)")
    @Test
    public void testUpdateParentLibelleBlank() {

        /* ARRANGE :
         * prépare un parent dont le libellé est blank,
         * afin de vérifier le contrôle applicatif
         * effectué avant toute recherche réelle du parent.
         */
        final TypeProduit parent = fabriquerTypeProduit(BLANK, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_1, parent);

        /* ACT - ASSERT :
         * vérifie que l'appel service.update(...)
         * avec un objet dont le parent a un libellé blank
         * jette une ExceptionAppliLibelleBlank
         * avec le message MSG_UPDATE_KO_LIBELLE_PARENT_BLANK
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(MSG_UPDATE_KO_LIBELLE_PARENT_BLANK);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * Assure que :
         * - typeProduitDaoJPA.findById(...) n'a jamais été appelée.
         * - sousTypeProduitDaoJPA.findById(...) n'a jamais été appelée.
         * - sousTypeProduitDaoJPA.save(...) n'a jamais été appelée.
         */
        verify(this.typeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(parent ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent ;</li>
     * <li>n'appelle ni le DAO parent ni le DAO enfant.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent ID null) - jette ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testUpdateParentIdNull() {

        /* ARRANGE :
         * prépare un parent non persistant
         * dont l'identifiant est null,
         * afin de vérifier le contrôle de persistance
         * effectué avant toute recherche DAO.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, null);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_1, parent);

        /* ACT - ASSERT :
         * vérifie que l'appel service.update(...)
         * avec un parent non persistant
         * jette une ExceptionTechniqueGatewayNonPersistent
         * avec un message contenant MSG_UPDATE_PREFIX_PARENT_NON_PERSISTENT
         * suivi du libellé du parent.
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(construireMessageNonPersistent(
                    MSG_UPDATE_PREFIX_PARENT_NON_PERSISTENT, LIBELLE_PARENT_1));

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * Assure que :
         * - typeProduitDaoJPA.findById(...) n'a jamais été appelée.
         * - sousTypeProduitDaoJPA.findById(...) n'a jamais été appelée.
         * - sousTypeProduitDaoJPA.save(...) n'a jamais été appelée.
         */
        verify(this.typeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(parent absent DAO) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGatewayNonPersistent} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT}
     * suivi du libellé du parent ;</li>
     * <li>appelle la méthode typeProduitDaoJPA.findById(...)
     * du DAO parent mocké avec Mockito ;</li>
     * <li>n'appelle ni la méthode sousTypeProduitDaoJPA.findById(...)
     * ni la méthode sousTypeProduitDaoJPA.save(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent absent DAO) - jette ExceptionTechniqueGatewayNonPersistent")
    @Test
    public void testUpdateParentAbsent() {

        /* ARRANGE :
         * prépare un parent persistant en apparence,
         * mais absent du DAO parent mocké avec Mockito.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        /* Condition du Mock :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké
         * retourne Optional.empty().
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.empty());

        /* ACT - ASSERT :
         * vérifie que :
         * this.service.update(stp)
         * avec typeProduitDaoJPA.findById(ID_1) qui retourne Optional.empty()
         * - jette une ExceptionTechniqueGatewayNonPersistent
         * - avec un message contenant MSG_UPDATE_PREFIX_PARENT_NON_PERSISTENT
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionTechniqueGatewayNonPersistent.class)
            .hasMessage(construireMessageNonPersistent(
                    MSG_UPDATE_PREFIX_PARENT_NON_PERSISTENT, LIBELLE_PARENT_1));

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - typeProduitDaoJPA.findById(ID_1) a été appelée une fois.
         * - sousTypeProduitDaoJPA.findById(...) n'a jamais été appelée.
         * - sousTypeProduitDaoJPA.save(...) n'a jamais été appelée.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA, never()).findById(anyLong());
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(absent) :</p>
     * <ul>
     * <li>retourne {@code null} ;</li>
     * <li>appelle la méthode typeProduitDaoJPA.findById(...)
     * du DAO parent mocké avec Mockito ;</li>
     * <li>appelle la méthode sousTypeProduitDaoJPA.findById(...)
     * du DAO enfant mocké avec Mockito ;</li>
     * <li>n'appelle pas la méthode sousTypeProduitDaoJPA.save(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     * @throws Exception 
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(absent) - retourne null")
    @Test
    public void testUpdateAbsent() throws Exception {

        /* ARRANGE :
         * prépare un parent existant,
         * puis configure le DAO enfant mocké avec Mockito
         * pour que l'objet à modifier soit absent du stockage.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findById(ID_2) sur le DAO mocké
         * retourne Optional.empty().
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.empty());

        /* ACT :
         * appelle this.service.update(stp)
         * dans le scénario où l'objet à modifier est absent du stockage.
         */
        final SousTypeProduit retour = this.service.update(stp);

        /* ASSERT :
         * vérifie que la méthode retourne null
         * lorsque l'objet à modifier est introuvable.
         */
        assertThat(retour).isNull();

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - typeProduitDaoJPA.findById(ID_1) a été appelée une fois.
         * - sousTypeProduitDaoJPA.findById(ID_2) a été appelée une fois.
         * - sousTypeProduitDaoJPA.save(...) n'a jamais été appelée.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).findById(ID_2);
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(findById retourne null Optional) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle la méthode typeProduitDaoJPA.findById(...)
     * du DAO parent mocké avec Mockito ;</li>
     * <li>appelle la méthode sousTypeProduitDaoJPA.findById(...)
     * du DAO enfant mocké avec Mockito ;</li>
     * <li>n'appelle pas la méthode sousTypeProduitDaoJPA.save(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(findById retourne null Optional) - jette ExceptionTechniqueGateway")
    @Test
    public void testUpdateDaoFindByIdRetourneNullOptional() {

        /* ARRANGE :
         * prépare un parent existant,
         * puis configure le DAO enfant mocké avec Mockito
         * pour que sousTypeProduitDaoJPA.findById(ID_2) retourne null.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findById(ID_2) sur le DAO mocké
         * retourne null.
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que :
         * this.service.update(stp)
         * avec sousTypeProduitDaoJPA.findById(ID_2) qui retourne null
         * - jette une ExceptionTechniqueGateway
         * - avec un message MSG_ERREUR_TECH_KO_STOCKAGE
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - typeProduitDaoJPA.findById(ID_1) a été appelée une fois.
         * - sousTypeProduitDaoJPA.findById(ID_2) a été appelée une fois.
         * - sousTypeProduitDaoJPA.save(...) n'a jamais été appelée.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).findById(ID_2);
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(sans modification) :</p>
     * <ul>
     * <li>retourne un objet métier non null ;</li>
     * <li>retourne le libellé enfant attendu ;</li>
     * <li>retourne un parent non null ;</li>
     * <li>retourne le libellé parent attendu ;</li>
     * <li>n'appelle pas la méthode sousTypeProduitDaoJPA.save(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     * @throws Exception 
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(sans modification) - ne déclenche pas de save()")
    @Test
    public void testUpdateSansModification() throws Exception {

        /* ARRANGE :
         * prépare un objet persistant en apparence
         * identique à l'objet déjà stocké,
         * afin de vérifier qu'aucune sauvegarde n'est nécessaire.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduitJPA persiste = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);

        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findById(ID_2) sur le DAO mocké
         * retourne Optional.of(persiste).
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.of(persiste));

        /* ACT :
         * appelle this.service.update(stp)
         * dans le scénario où aucune modification n'est détectée.
         */
        final SousTypeProduit retour = this.service.update(stp);

        /* ASSERT :
         * vérifie que la méthode retourne bien
         * l'objet métier inchangé.
         */
        assertThat(retour).isNotNull();
        assertThat(retour.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_1);
        assertThat(retour.getTypeProduit()).isNotNull();
        assertThat(retour.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_1);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - typeProduitDaoJPA.findById(ID_1) a été appelée une fois.
         * - sousTypeProduitDaoJPA.findById(ID_2) a été appelée une fois.
         * - sousTypeProduitDaoJPA.save(...) n'a jamais été appelée.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).findById(ID_2);
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(avec modification) :</p>
     * <ul>
     * <li>retourne un objet métier non null ;</li>
     * <li>retourne le nouveau libellé enfant attendu ;</li>
     * <li>retourne un parent non null ;</li>
     * <li>retourne le nouveau libellé parent attendu ;</li>
     * <li>appelle la méthode sousTypeProduitDaoJPA.save(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     * @throws Exception 
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(avec modification) - appelle save() et retourne l'objet modifié")
    @Test
    public void testUpdateAvecModification() throws Exception {

        /* ARRANGE :
         * prépare un objet persistant en apparence
         * portant un nouveau libellé enfant
         * et un nouveau parent persistant.
         */
        final TypeProduit parentNouveau = fabriquerTypeProduit(LIBELLE_PARENT_2, ID_2);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_2, ID_3, parentNouveau);

        final TypeProduitJPA parentJPA2 = fabriquerTypeProduitJPA(LIBELLE_PARENT_2, ID_2);
        final TypeProduitJPA parentJPA1 = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduitJPA persiste = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_3, parentJPA1);
        final SousTypeProduitJPA sauvegarde = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_2, ID_3, parentJPA2);

        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_2) sur le DAO mocké
         * retourne Optional.of(parentJPA2).
         */
        when(this.typeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.of(parentJPA2));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findById(ID_3) sur le DAO mocké
         * retourne Optional.of(persiste).
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_3)).thenReturn(Optional.of(persiste));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.save(...) sur le DAO mocké
         * retourne sauvegarde.
         */
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class))).thenReturn(sauvegarde);

        /* ACT :
         * appelle this.service.update(stp)
         * dans le scénario nominal avec modification.
         */
        final SousTypeProduit retour = this.service.update(stp);

        /* ASSERT :
         * vérifie que la méthode retourne bien
         * l'objet métier modifié et sauvegardé.
         */
        assertThat(retour).isNotNull();
        assertThat(retour.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_2);
        assertThat(retour.getTypeProduit()).isNotNull();
        assertThat(retour.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_2);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - typeProduitDaoJPA.findById(ID_2) a été appelée une fois.
         * - sousTypeProduitDaoJPA.findById(ID_3) a été appelée une fois.
         * - sousTypeProduitDaoJPA.save(...) a été appelée une fois.
         */
        verify(this.typeProduitDaoJPA).findById(ID_2);
        verify(this.sousTypeProduitDaoJPA).findById(ID_3);
        verify(this.sousTypeProduitDaoJPA).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(save retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle la méthode typeProduitDaoJPA.findById(...)
     * du DAO parent mocké avec Mockito ;</li>
     * <li>appelle la méthode sousTypeProduitDaoJPA.findById(...)
     * du DAO enfant mocké avec Mockito ;</li>
     * <li>appelle la méthode sousTypeProduitDaoJPA.save(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(save retourne null) - jette ExceptionTechniqueGateway")
    @Test
    public void testUpdateSaveRetourneNull() {

        /* ARRANGE :
         * prépare un objet à modifier
         * réellement différent de l'objet persistant,
         * puis configure le DAO enfant mocké avec Mockito
         * pour que save(...) retourne null.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_2, ID_2, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduitJPA persiste = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);

        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findById(ID_2) sur le DAO mocké
         * retourne Optional.of(persiste).
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.of(persiste));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.save(...) sur le DAO mocké
         * retourne null.
         */
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class))).thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que :
         * this.service.update(stp)
         * avec sousTypeProduitDaoJPA.save(...) qui retourne null
         * - jette une ExceptionTechniqueGateway
         * - avec un message MSG_ERREUR_TECH_KO_STOCKAGE
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.update(stp))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - typeProduitDaoJPA.findById(ID_1) a été appelée une fois.
         * - sousTypeProduitDaoJPA.findById(ID_2) a été appelée une fois.
         * - sousTypeProduitDaoJPA.save(...) a été appelée une fois.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).findById(ID_2);
        verify(this.sousTypeProduitDaoJPA).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(KO DAO save message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle la méthode typeProduitDaoJPA.findById(...)
     * du DAO parent mocké avec Mockito ;</li>
     * <li>appelle la méthode sousTypeProduitDaoJPA.findById(...)
     * du DAO enfant mocké avec Mockito ;</li>
     * <li>appelle la méthode sousTypeProduitDaoJPA.save(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(KO DAO save message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testUpdateDaoSaveExceptionMessageNonNull() {

        /* ARRANGE :
         * prépare un objet à modifier
         * réellement différent de l'objet persistant,
         * puis configure le DAO enfant mocké avec Mockito
         * pour que save(...) jette une RuntimeException
         * avec message non null.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_2, ID_2, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduitJPA persiste = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);
        final RuntimeException ex = new RuntimeException(LIBELLE_ENFANT_2);

        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findById(ID_2) sur le DAO mocké
         * retourne Optional.of(persiste).
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.of(persiste));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.save(...) sur le DAO mocké
         * jette l'Exception ex.
         */
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class))).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.update(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.update(...)
         * - intercepte toute exception éventuellement levée
         * - puis stocke cette exception dans la variable throwable
         *   de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.update(stp));

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
         * - typeProduitDaoJPA.findById(ID_1) a été appelée une fois.
         * - sousTypeProduitDaoJPA.findById(ID_2) a été appelée une fois.
         * - sousTypeProduitDaoJPA.save(...) a été appelée une fois.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).findById(ID_2);
        verify(this.sousTypeProduitDaoJPA).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(KO DAO save message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle la méthode typeProduitDaoJPA.findById(...)
     * du DAO parent mocké avec Mockito ;</li>
     * <li>appelle la méthode sousTypeProduitDaoJPA.findById(...)
     * du DAO enfant mocké avec Mockito ;</li>
     * <li>appelle la méthode sousTypeProduitDaoJPA.save(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(KO DAO save message null) - jette ExceptionTechniqueGateway")
    @Test
    public void testUpdateDaoSaveExceptionMessageNull() {

        /* ARRANGE :
         * prépare un objet à modifier
         * réellement différent de l'objet persistant,
         * puis configure le DAO enfant mocké avec Mockito
         * pour que save(...) jette une RuntimeException
         * sans message.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_2, ID_2, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduitJPA persiste = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);
        final RuntimeException ex = new RuntimeException((String) null);

        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_1) sur le DAO mocké
         * retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findById(ID_2) sur le DAO mocké
         * retourne Optional.of(persiste).
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.of(persiste));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.save(...) sur le DAO mocké
         * jette l'Exception ex.
         */
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class))).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.update(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.update(...)
         * - intercepte toute exception éventuellement levée
         * - puis stocke cette exception dans la variable throwable
         *   de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.update(stp));

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
         * - typeProduitDaoJPA.findById(ID_1) a été appelée une fois.
         * - sousTypeProduitDaoJPA.findById(ID_2) a été appelée une fois.
         * - sousTypeProduitDaoJPA.save(...) a été appelée une fois.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).findById(ID_2);
        verify(this.sousTypeProduitDaoJPA).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(parent modifié) :</p>
     * <ul>
     * <li>retourne un objet métier non null ;</li>
     * <li>retourne le libellé enfant attendu ;</li>
     * <li>retourne le nouveau parent attendu ;</li>
     * <li>appelle la méthode sousTypeProduitDaoJPA.save(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     * @throws Exception 
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent modifié) - met à jour le parent")
    @Test
    public void testUpdateParentModifie() throws Exception {

        /* ARRANGE :
         * prépare un objet persistant en apparence
         * dont le parent change,
         * mais dont le libellé enfant reste identique.
         */
        final TypeProduit parentNouveau = fabriquerTypeProduit(LIBELLE_PARENT_2, ID_2);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_3, parentNouveau);

        final TypeProduitJPA parentJPA1 = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final TypeProduitJPA parentJPA2 = fabriquerTypeProduitJPA(LIBELLE_PARENT_2, ID_2);
        final SousTypeProduitJPA persiste = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_3, parentJPA1);
        final SousTypeProduitJPA sauvegarde = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_3, parentJPA2);

        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_2) sur le DAO mocké
         * retourne Optional.of(parentJPA2).
         */
        when(this.typeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.of(parentJPA2));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findById(ID_3) sur le DAO mocké
         * retourne Optional.of(persiste).
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_3)).thenReturn(Optional.of(persiste));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.save(...) sur le DAO mocké
         * retourne sauvegarde.
         */
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class))).thenReturn(sauvegarde);

        /* ACT :
         * appelle this.service.update(stp)
         * dans le scénario où seul le parent est modifié.
         */
        final SousTypeProduit retour = this.service.update(stp);

        /* ASSERT :
         * vérifie que la méthode retourne bien
         * un objet métier cohérent avec le nouveau parent.
         */
        assertThat(retour).isNotNull();
        assertThat(retour.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_1);
        assertThat(retour.getTypeProduit()).isNotNull();
        assertThat(retour.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_2);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - typeProduitDaoJPA.findById(ID_2) a été appelée une fois.
         * - sousTypeProduitDaoJPA.findById(ID_3) a été appelée une fois.
         * - sousTypeProduitDaoJPA.save(...) a été appelée une fois.
         */
        verify(this.typeProduitDaoJPA).findById(ID_2);
        verify(this.sousTypeProduitDaoJPA).findById(ID_3);
        verify(this.sousTypeProduitDaoJPA).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________    
    

    
    // ============================= delete ===============================
    
    
    
    /**
     * <div>
     * <p>garantit que delete(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_DELETE_KO_PARAM_NULL} ;</li>
     * <li>n'appelle ni la méthode sousTypeProduitDaoJPA.findById(...)
     * ni les méthodes entityManager.remove(...) et entityManager.flush().</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
	@Tag(TAG_DELETE)
    @DisplayName("delete(null) - jette ExceptionAppliParamNull (contrat du port)")
    @Test
    public void testDeleteNull() {

        /* ARRANGE - ACT - ASSERT :
         * vérifie que l'appel service.delete(...) avec un paramètre null
         * jette une ExceptionAppliParamNull
         * avec le message MSG_DELETE_KO_PARAM_NULL
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.delete(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(MSG_DELETE_KO_PARAM_NULL);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * Assure que :
         * - sousTypeProduitDaoJPA.findById(...) n'a jamais été appelée.
         * - entityManager.remove(...) n'a jamais été appelée.
         * - entityManager.flush() n'a jamais été appelée.
         */
        verify(this.sousTypeProduitDaoJPA, never()).findById(anyLong());
        verify(this.entityManager, never()).remove(any(SousTypeProduitJPA.class));
        verify(this.entityManager, never()).flush();

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNonPersistent} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#MESSAGE_DELETE_KO_ID_NULL} ;</li>
     * <li>n'appelle ni la méthode sousTypeProduitDaoJPA.findById(...)
     * ni les méthodes entityManager.remove(...) et entityManager.flush().</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
	@Tag(TAG_DELETE)
    @DisplayName("delete(ID null) - jette ExceptionAppliParamNonPersistent (contrat du port)")
    @Test
    public void testDeleteIdNull() {

        /* ARRANGE :
         * prépare un objet métier non persistant
         * dont l'identifiant est null,
         * afin de vérifier le contrôle de persistance
         * effectué avant toute recherche DAO.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        /* ACT - ASSERT :
         * vérifie que l'appel service.delete(...)
         * avec un objet métier ayant un ID null
         * jette une ExceptionAppliParamNonPersistent
         * avec le message MSG_DELETE_KO_ID_NULL
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.delete(stp))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(MSG_DELETE_KO_ID_NULL);

        /*
         * Vérifie ensuite qu'aucun accès au stockage
         * n'a été tenté pour ce scénario traité
         * par la gestion des mauvais paramètres avant tout appel des DAO.
         * Assure que :
         * - sousTypeProduitDaoJPA.findById(...) n'a jamais été appelée.
         * - entityManager.remove(...) n'a jamais été appelée.
         * - entityManager.flush() n'a jamais été appelée.
         */
        verify(this.sousTypeProduitDaoJPA, never()).findById(anyLong());
        verify(this.entityManager, never()).remove(any(SousTypeProduitJPA.class));
        verify(this.entityManager, never()).flush();

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(findById retourne null Optional) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet le message
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE} ;</li>
     * <li>appelle la méthode sousTypeProduitDaoJPA.findById(...)
     * du DAO enfant mocké avec Mockito ;</li>
     * <li>n'appelle ni entityManager.remove(...) ni entityManager.flush().</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
	@Tag(TAG_DELETE)
    @DisplayName("delete(findById retourne null Optional) - jette ExceptionTechniqueGateway")
    @Test
    public void testDeleteDaoFindByIdRetourneNullOptional() {

        /* ARRANGE :
         * prépare un objet métier persistant en apparence,
         * puis configure le DAO enfant mocké avec Mockito
         * pour que sousTypeProduitDaoJPA.findById(ID_2) retourne null.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(ID_2) sur le DAO mocké
         * retourne null.
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(null);

        /* ACT - ASSERT :
         * vérifie que :
         * this.service.delete(stp)
         * avec sousTypeProduitDaoJPA.findById(ID_2) qui retourne null
         * - jette une ExceptionTechniqueGateway
         * - avec un message MSG_ERREUR_TECH_KO_STOCKAGE
         * (message contractuel du port).
         */
        assertThatThrownBy(() -> this.service.delete(stp))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /*
         * Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - sousTypeProduitDaoJPA.findById(ID_2) a été appelée une fois.
         * - entityManager.remove(...) n'a jamais été appelée.
         * - entityManager.flush() n'a jamais été appelée.
         */
        verify(this.sousTypeProduitDaoJPA).findById(ID_2);
        verify(this.entityManager, never()).remove(any(SousTypeProduitJPA.class));
        verify(this.entityManager, never()).flush();

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(absent) :</p>
     * <ul>
     * <li>ne jette pas d'exception ;</li>
     * <li>ne fait rien ;</li>
     * <li>appelle une seule fois la méthode sousTypeProduitDaoJPA.findById(...)
     * du DAO enfant mocké avec Mockito ;</li>
     * <li>n'appelle ni entityManager.remove(...) ni entityManager.flush().</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @SuppressWarnings(RESOURCE)
	@Tag(TAG_DELETE)
    @DisplayName("delete(absent) - ne fait rien")
    @Test
    public void testDeleteAbsent() throws Exception {

        /* ARRANGE :
         * prépare un objet métier persistant en apparence,
         * puis configure le DAO enfant mocké avec Mockito
         * pour que l'objet à supprimer soit absent du stockage.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(ID_2) sur le DAO mocké
         * retourne Optional.empty().
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.empty());

        /* ACT :
         * appelle this.service.delete(stp)
         * dans le scénario où l'objet à supprimer est absent du stockage.
         */
        this.service.delete(stp);

        /*
         * ASSERT :
         * vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - sousTypeProduitDaoJPA.findById(ID_2) a été appelée une fois.
         * - entityManager.remove(...) n'a jamais été appelée.
         * - entityManager.flush() n'a jamais été appelée.
         */
        verify(this.sousTypeProduitDaoJPA).findById(ID_2);
        verify(this.entityManager, never()).remove(any(SousTypeProduitJPA.class));
        verify(this.entityManager, never()).flush();

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(nominal) :</p>
     * <ul>
     * <li>ne jette pas d'exception ;</li>
     * <li>appelle entityManager.remove(...) sur l'entité trouvée ;</li>
     * <li>appelle entityManager.flush() ;</li>
     * <li>vérifie la suppression par un second appel
     * à sousTypeProduitDaoJPA.findById(...).</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @SuppressWarnings(RESOURCE)
    @Tag(TAG_DELETE)
    @DisplayName("delete(nominal) - supprime l'entité et vérifie la suppression")
    @Test
    public void testDeleteNominal() throws Exception {

        /* ARRANGE :
         * prépare un objet métier persistant en apparence
         * et une entité JPA correspondante.
         */
        final Long id = ID_2;
        final SousTypeProduit stp =
                fabriquerSousTypeProduit(LIBELLE_ENFANT_1, id,
                        fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1));

        final SousTypeProduitJPA entity =
                fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, id,
                        fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1));

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(ID_2) sur le DAO mocké
         * retourne l'entité avant suppression,
         * puis Optional.empty() après suppression.
         */
        when(this.sousTypeProduitDaoJPA.findById(id))
            .thenReturn(Optional.of(entity))
            .thenReturn(Optional.empty());

        /* Condition du Mock EntityManager :
         * Les appels entityManager.remove(...) et entityManager.flush()
         * ne font rien.
         */
        doNothing().when(this.entityManager).remove(any(SousTypeProduitJPA.class));
        doNothing().when(this.entityManager).flush();

        /* ACT :
         * appelle this.service.delete(stp)
         * dans le scénario nominal de suppression.
         */
        this.service.delete(stp);

        /* ASSERT :
         * vérifie que la suppression a bien été demandée
         * puis validée par flush().
         */
        verify(this.entityManager).remove(entity);
        verify(this.entityManager).flush();

        /* Vérifie aussi que la méthode findById()
         * du DAO enfant mocké avec Mockito
         * a bien été appelée deux fois :
         * - une fois avant suppression ;
         * - une fois après suppression pour contrôle.
         */
        verify(this.sousTypeProduitDaoJPA, times(2)).findById(id);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(vérification post-suppression échoue) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message indiquant l'échec de la suppression ;</li>
     * <li>appelle entityManager.remove(...) et entityManager.flush() ;</li>
     * <li>appelle deux fois sousTypeProduitDaoJPA.findById(...).</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
    @Tag(TAG_DELETE)
    @DisplayName("delete(vérification post-suppression échoue) - jette ExceptionTechniqueGateway")
    @Test
    public void testDeleteVerificationPostSuppressionEchoue() {

        /* ARRANGE :
         * prépare un objet métier persistant en apparence
         * et simule un échec de suppression :
         * l'entité est encore retrouvée après remove(...) et flush().
         */
        final SousTypeProduit stp =
                fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2,
                        fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1));

        final SousTypeProduitJPA entity =
                fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2,
                        fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1));

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(ID_2) sur le DAO mocké
         * retourne l'entité avant suppression
         * puis retourne encore l'entité après suppression simulée.
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.of(entity))
            .thenReturn(Optional.of(entity));

        doNothing().when(this.entityManager).remove(any(SousTypeProduitJPA.class));
        doNothing().when(this.entityManager).flush();

        /* ACT :
         * sollicite la méthode service.delete(...)
         * dans le scénario où la vérification post-suppression échoue.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.delete(stp));

        /* ASSERT :
         * vérifie que l'exception technique observable
         * signale bien l'échec de la suppression.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageContaining("Échec de la suppression");

        verify(this.entityManager).remove(entity);
        verify(this.entityManager).flush();
        verify(this.sousTypeProduitDaoJPA, times(2)).findById(ID_2);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(KO DAO findById message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle une seule fois la méthode sousTypeProduitDaoJPA.findById(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
	@Tag(TAG_DELETE)
    @DisplayName("delete(KO DAO findById message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testDeleteDaoFindByIdExceptionMessageNonNull() {

        /* ARRANGE :
         * prépare un objet métier persistant en apparence,
         * puis configure le DAO enfant mocké avec Mockito
         * pour que sousTypeProduitDaoJPA.findById(ID_2)
         * jette une RuntimeException avec message non null.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);
        final RuntimeException ex = new RuntimeException(LIBELLE_ENFANT_1);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(ID_2) sur le DAO mocké
         * jette l'Exception ex.
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.delete(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.delete(...)
         * - intercepte toute exception éventuellement levée
         * - puis stocke cette exception dans la variable throwable
         *   de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.delete(stp));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable.getCause()).isSameAs(ex);

        verify(this.sousTypeProduitDaoJPA).findById(ID_2);
        verify(this.entityManager, never()).remove(any(SousTypeProduitJPA.class));
        verify(this.entityManager, never()).flush();

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que delete(KO DAO findById message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle une seule fois la méthode sousTypeProduitDaoJPA.findById(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @SuppressWarnings(RESOURCE)
	@Tag(TAG_DELETE)
    @DisplayName("delete(KO DAO findById message null) - jette ExceptionTechniqueGateway")
    @Test
    public void testDeleteDaoFindByIdExceptionMessageNull() {

        /* ARRANGE :
         * prépare un objet métier persistant en apparence,
         * puis configure le DAO enfant mocké avec Mockito
         * pour que sousTypeProduitDaoJPA.findById(ID_2)
         * jette une RuntimeException sans message.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);
        final RuntimeException ex = new RuntimeException((String) null);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.findById(ID_2) sur le DAO mocké
         * jette l'Exception ex.
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenThrow(ex);

        /* ACT :
         * sollicite la méthode service.delete(...)
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.delete(...)
         * - intercepte toute exception éventuellement levée
         * - puis stocke cette exception dans la variable throwable
         *   de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.delete(stp));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable.getCause()).isSameAs(ex);

        verify(this.sousTypeProduitDaoJPA).findById(ID_2);
        verify(this.entityManager, never()).remove(any(SousTypeProduitJPA.class));
        verify(this.entityManager, never()).flush();

    } // __________________________________________________________________    
    
    

    // ============================== Count ===============================
    
    
    
    /**
     * <div>
     * <p>garantit que count(nominal) :</p>
     * <ul>
     * <li>retourne le compteur renvoyé par le DAO ;</li>
     * <li>appelle une seule fois la méthode sousTypeProduitDaoJPA.count()
     * du DAO mocké avec Mockito.</li>
     * </ul>
     * </div>
     * @throws Exception 
     */
    @Tag(TAG_COUNT)
    @DisplayName("count(nominal) - retourne le compteur DAO")
    @Test
    public void testCountNominalOk() throws Exception {

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.count()
         * sur le DAO mocké retourne TOTAL_10.
         */
        when(this.sousTypeProduitDaoJPA.count()).thenReturn(TOTAL_10);

        /* ACT :
         * appelle this.service.count()
         * dans le scénario nominal.
         */
        final long retour = this.service.count();

        /* ASSERT :
         * vérifie que la méthode retourne bien
         * le compteur fourni par le DAO mocké.
         */
        assertThat(retour).isEqualTo(TOTAL_10);

        /* Vérifie que la méthode sousTypeProduitDaoJPA.count()
         * du DAO mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).count();

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que count(zéro) :</p>
     * <ul>
     * <li>retourne zéro ;</li>
     * <li>appelle une seule fois la méthode sousTypeProduitDaoJPA.count()
     * du DAO mocké avec Mockito.</li>
     * </ul>
     * </div>
     * @throws Exception 
     */
    @Tag(TAG_COUNT)
    @DisplayName("count(zéro) - retourne zéro")
    @Test
    public void testCountZero() throws Exception {

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.count()
         * sur le DAO mocké retourne 0L.
         */
        when(this.sousTypeProduitDaoJPA.count()).thenReturn(0L);

        /* ACT :
         * appelle this.service.count()
         * dans le scénario où le DAO retourne zéro.
         */
        final long retour = this.service.count();

        /* ASSERT :
         * vérifie que la méthode retourne bien zéro.
         */
        assertThat(retour).isZero();

        /* Vérifie que la méthode sousTypeProduitDaoJPA.count()
         * du DAO mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).count();

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que count(KO DAO message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle une seule fois la méthode sousTypeProduitDaoJPA.count()
     * du DAO mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_COUNT)
    @DisplayName("count(KO DAO message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testCountDaoExceptionMessageNonNull() {

        /* ARRANGE :
         * configure le DAO mocké avec Mockito
         * pour que sousTypeProduitDaoJPA.count()
         * jette une RuntimeException avec message non null.
         */
        final RuntimeException ex = new RuntimeException(LIBELLE_ENFANT_1);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.count()
         * sur le DAO mocké jette l'Exception ex.
         */
        when(this.sousTypeProduitDaoJPA.count()).thenThrow(ex);

        /* ACT :
         * sollicite la méthode this.service.count()
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.count()
         * - intercepte toute exception éventuellement levée
         * - puis stocke cette exception dans la variable throwable
         *   de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.count());

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable.getCause()).isSameAs(ex);

        /* Vérifie que la méthode sousTypeProduitDaoJPA.count()
         * du DAO mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).count();

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que count(KO DAO message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle une seule fois la méthode sousTypeProduitDaoJPA.count()
     * du DAO mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_COUNT)
    @DisplayName("count(KO DAO message null) - jette ExceptionTechniqueGateway")
    @Test
    public void testCountDaoExceptionMessageNull() {

        /* ARRANGE :
         * configure le DAO mocké avec Mockito
         * pour que sousTypeProduitDaoJPA.count()
         * jette une RuntimeException sans message.
         */
        final RuntimeException ex = new RuntimeException((String) null);

        /* Condition du Mock :
         * L'appel sousTypeProduitDaoJPA.count()
         * sur le DAO mocké jette l'Exception ex.
         */
        when(this.sousTypeProduitDaoJPA.count()).thenThrow(ex);

        /* ACT :
         * sollicite la méthode this.service.count()
         * dans les conditions imposées par le mock (clause when).
         * - exécute this.service.count()
         * - intercepte toute exception éventuellement levée
         * - puis stocke cette exception dans la variable throwable
         *   de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.count());

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable.getCause()).isSameAs(ex);

        /* Vérifie que la méthode sousTypeProduitDaoJPA.count()
         * du DAO mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).count();

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
    

    
    // ============== TESTS SUPPLEMENTAIRES (nouveaux cas limites) ========



    /**
     * <div>
     * <p>garantit que delete(EntityManager.remove jette RuntimeException avec message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle une seule fois la méthode sousTypeProduitDaoJPA.findById(...)</li>
     * <li>appelle une seule fois la méthode entityManager.remove(...)</li>
     * <li>n'appelle jamais la méthode entityManager.flush().</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @SuppressWarnings(RESOURCE)
    @Tag(TAG_CAS_LIMITES)
    @DisplayName("delete(EntityManager.remove KO message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testDeleteEntityManagerRemoveJetteException() throws Exception {

        /* ARRANGE :
         * prépare un objet métier persistant en apparence
         * et l'entité JPA correspondante,
         * puis configure le DAO mocké avec Mockito
         * pour que la recherche par ID retourne cette entité.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final SousTypeProduitJPA entity =
                fabriquerSousTypeProduitJPA(
                        LIBELLE_ENFANT_1,
                        ID_2,
                        fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findById(ID_2)
         * sur le DAO mocké retourne Optional.of(entity).
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.of(entity));

        final RuntimeException ex = new RuntimeException("Erreur simulee par remove()");

        /* Condition du Mock entityManager :
         * L'appel entityManager.remove(entity)
         * sur l'EntityManager mocké jette l'Exception ex.
         */
        doThrow(ex).when(this.entityManager).remove(entity);

        /* ACT :
         * sollicite la méthode service.delete(...)
         * dans les conditions imposées par le mock.
         * - exécute this.service.delete(stp)
         * - intercepte toute exception éventuellement levée
         * - puis stocke cette exception dans la variable throwable
         *   de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.delete(stp));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable.getCause()).isSameAs(ex);

        /* Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - sousTypeProduitDaoJPA.findById(ID_2) a été appelée une fois.
         * - entityManager.remove(entity) a été appelée une fois.
         * - entityManager.flush() n'a jamais été appelée.
         */
        verify(this.sousTypeProduitDaoJPA).findById(ID_2);
        verify(this.entityManager).remove(entity);
        verify(this.entityManager, never()).flush();

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(EntityManager.flush jette RuntimeException avec message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link SousTypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>propage une cause non null ;</li>
     * <li>appelle une seule fois la méthode sousTypeProduitDaoJPA.findById(...)</li>
     * <li>appelle une seule fois la méthode entityManager.remove(...)</li>
     * <li>appelle une seule fois la méthode entityManager.flush().</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @SuppressWarnings(RESOURCE)
    @Tag(TAG_CAS_LIMITES)
    @DisplayName("delete(EntityManager.flush KO message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testDeleteEntityManagerFlushJetteException() throws Exception {

        /* ARRANGE :
         * prépare un objet métier persistant en apparence
         * et l'entité JPA correspondante,
         * puis configure le DAO mocké avec Mockito
         * pour que la recherche par ID retourne cette entité.
         */
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final SousTypeProduitJPA entity =
                fabriquerSousTypeProduitJPA(
                        LIBELLE_ENFANT_1,
                        ID_2,
                        fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findById(ID_2)
         * sur le DAO mocké retourne Optional.of(entity).
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.of(entity));

        /* Condition du Mock entityManager :
         * L'appel entityManager.remove(entity)
         * sur l'EntityManager mocké ne fait rien.
         */
        doNothing().when(this.entityManager).remove(entity);

        final RuntimeException ex = new RuntimeException("Erreur simulee par flush()");

        /* Condition du Mock entityManager :
         * L'appel entityManager.flush()
         * sur l'EntityManager mocké jette l'Exception ex.
         */
        doThrow(ex).when(this.entityManager).flush();

        /* ACT :
         * sollicite la méthode service.delete(...)
         * dans les conditions imposées par le mock.
         * - exécute this.service.delete(stp)
         * - intercepte toute exception éventuellement levée
         * - puis stocke cette exception dans la variable throwable
         *   de type Throwable.
         */
        final Throwable throwable =
                Assertions.catchThrowable(() -> this.service.delete(stp));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son préfixe contractuel et la cause propagée.
         */
        assertThat(throwable).isInstanceOf(ExceptionTechniqueGateway.class);
        assertThat(throwable).hasMessageStartingWith(MSG_PREFIX_ERREUR_TECH);
        assertThat(throwable.getCause()).isSameAs(ex);

        /* Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - sousTypeProduitDaoJPA.findById(ID_2) a été appelée une fois.
         * - entityManager.remove(entity) a été appelée une fois.
         * - entityManager.flush() a été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA).findById(ID_2);
        verify(this.entityManager).remove(entity);
        verify(this.entityManager).flush();

    } // __________________________________________________________________
    

    
    /**
     * <div>
     * <p>garantit que update(parent avec différence de casse seulement) :</p>
     * <ul>
     * <li>retourne un objet métier non null ;</li>
     * <li>retourne le libellé enfant attendu ;</li>
     * <li>retourne le libellé parent normalisé selon le parent persistant ;</li>
     * <li>appelle la méthode typeProduitDaoJPA.findById(...) du DAO parent mocké avec Mockito ;</li>
     * <li>appelle la méthode sousTypeProduitDaoJPA.findById(...) du DAO enfant mocké avec Mockito ;</li>
     * <li>n'appelle pas la méthode sousTypeProduitDaoJPA.save(...)
     * du DAO enfant mocké avec Mockito.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(parent avec différence de casse seulement) - ne déclenche pas de save()")
    @Test
    public void testUpdateParentLibelleCaseSensitive() throws Exception {

        /* ARRANGE :
         * prépare un sous-type dont le parent porte
         * le même libellé fonctionnel que le parent persistant,
         * mais avec une casse différente.
         *
         * Ce cas limite vérifie que le service
         * ne considère pas cette différence de casse seule
         * comme une modification nécessitant une sauvegarde.
         */
        final TypeProduit parent =
                fabriquerTypeProduit(
                        LIBELLE_PARENT_1.toUpperCase(LOCALE_DEFAUT),
                        ID_1);
        final SousTypeProduit stp =
                fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final TypeProduitJPA parentJPA =
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        final SousTypeProduitJPA persiste =
                fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);

        /* Condition du Mock typeProduitDaoJPA :
         * L'appel typeProduitDaoJPA.findById(ID_1)
         * sur le DAO mocké retourne Optional.of(parentJPA).
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        /* Condition du Mock sousTypeProduitDaoJPA :
         * L'appel sousTypeProduitDaoJPA.findById(ID_2)
         * sur le DAO mocké retourne Optional.of(persiste).
         */
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.of(persiste));

        /* ACT :
         * appelle this.service.update(stp)
         * dans le scénario où seul le libellé du parent
         * diffère par la casse.
         */
        final SousTypeProduit retour = this.service.update(stp);

        /* ASSERT :
         * vérifie que la méthode retourne bien
         * un objet métier cohérent
         * sans déclencher de sauvegarde inutile.
         */
        assertThat(retour).isNotNull();
        assertThat(retour.getSousTypeProduit()).isEqualTo(LIBELLE_ENFANT_1);
        assertThat(retour.getTypeProduit()).isNotNull();
        assertThat(retour.getTypeProduit().getTypeProduit()).isEqualTo(LIBELLE_PARENT_1);

        /* Vérifie ensuite les interactions réelles
         * avec les dépendances mockées.
         * Assure que :
         * - typeProduitDaoJPA.findById(ID_1) a été appelée une fois.
         * - sousTypeProduitDaoJPA.findById(ID_2) a été appelée une fois.
         * - sousTypeProduitDaoJPA.save(...) n'a jamais été appelée.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.sousTypeProduitDaoJPA).findById(ID_2);
        verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelleRapide(contenu introuvable) :</p>
     * <ul>
     * <li>retourne une liste non null ;</li>
     * <li>retourne une liste vide ;</li>
     * <li>appelle une seule fois la méthode
     * sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase(...)
     * du DAO mocké avec Mockito.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYLIBELLERAPIDE)
    @DisplayName("findByLibelleRapide(contenu introuvable) - retourne une liste vide")
    @Test
    public void testFindByLibelleRapideContenuIntrouvable() throws Exception {

        /* ARRANGE :
         * configure le DAO mocké avec Mockito
         * pour que la méthode
         * sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase(\"INCONNU\")
         * retourne une liste vide.
         */
        when(this.sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase("INCONNU"))
            .thenReturn(new ArrayList<SousTypeProduitJPA>());

        /* ACT :
         * appelle this.service.findByLibelleRapide("INCONNU")
         * dans le scénario où aucun sous-type
         * ne correspond au contenu recherché.
         */
        final List<SousTypeProduit> retour =
                this.service.findByLibelleRapide("INCONNU");

        /* ASSERT :
         * vérifie que la méthode retourne bien
         * une liste non nulle, mais vide.
         */
        assertThat(retour).isNotNull().isEmpty();

        /* Vérifie que la méthode
         * sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase("INCONNU")
         * du DAO mocké avec Mockito a bien été appelée une fois.
         */
        verify(this.sousTypeProduitDaoJPA)
            .findBySousTypeProduitContainingIgnoreCase("INCONNU");

    } // __________________________________________________________________



    /**
     * <div>
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
