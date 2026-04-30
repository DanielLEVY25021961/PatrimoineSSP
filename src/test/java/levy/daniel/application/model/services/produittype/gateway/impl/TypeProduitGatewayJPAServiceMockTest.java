/* ********************************************************************* */
/* ********************* TEST MOCKITO GATEWAY JPA ********************** */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.gateway.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliLibelleBlank;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNonPersistent;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionAppliParamNull;
import levy.daniel.application.model.services.produittype.exceptionsgateway.ExceptionTechniqueGateway;
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
 * CLASSE TypeProduitGatewayJPAServiceMockTest.java :
 * </p>
 *
 * <ul>
 * <li>Tests JUnit Mockito complets du
 * SERVICE GATEWAY JPA
 * {@link TypeProduitGatewayJPAService}.</li>
 * <li>Vérifie l'implémentation des contrats du PORT
 * {@link TypeProduitGatewayIService} et le dialogue avec
 * le DAO JPA {@link TypeProduitDaoJPA}.</li>
 * <li>Aucun profil Spring n'est activé dans cette classe,
 * car ce test Mockito ne démarre pas de contexte Spring :
 * le DAO JPA est mocké à la main
 * et le SERVICE GATEWAY JPA est instancié directement
 * dans la méthode {@code init()}.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 6.0
 * @since 2 février 2026
 */
@ExtendWith(MockitoExtension.class)
public class TypeProduitGatewayJPAServiceMockTest {

    // ************************** CONSTANTES *****************************/
	
	/**
	 * <div>
	 * <p>"unchecked".</p>
	 * </div>
	 */
	public static final String UNCHECKED = "unchecked";
	
	/**
	 * "convertirRequetePageEnPageable"
	 */
	public static final String CONV_REQ_PAGE 
		= "convertirRequetePageEnPageable";
	
	/**
	 * "filtrerTrierDedoublonner"
	 */
	public static final String FILTRERTRIER = "filtrerTrierDedoublonner";
	
	/**
	 * "safeEquals"
	 */
	public static final String SAFE_EQUALS = "safeEquals";
	
	/**
	 * "isBlank"
	 */
	public static final String ISBLANK = "isBlank";
	
	/**
	 * "appliquerModifications"
	 */
	public static final String APPLIQUER_MODIFS = "appliquerModifications";

    /**
     * "servicesGateway-Creer"
     */
    public static final String TAG_CREER = "servicesGateway-Creer";
    
    /**
     * "servicesGateway-Rechercher"
     */
    public static final String TAG_RECHERCHER = "servicesGateway-Rechercher";
    
    /**
     * "servicesGateway-FindByObjetMetier"
     */
    public static final String TAG_FINDBYOBJETMETIER 
    	= "servicesGateway-FindByObjetMetier";
   
    /**
     * "servicesGateway-RechercherRapide"
     */
    public static final String TAG_RECHERCHER_RAPIDE 
    	= "servicesGateway-RechercherRapide";
    
    /**
     * "servicesGateway-Pagination"
     */
    public static final String TAG_PAGINATION 
    	= "servicesGateway-Pagination";
   
    /**
     * "servicesGateway-Update"
     */
    public static final String TAG_UPDATE = "servicesGateway-Update";
   
    /**
     * "servicesGateway-Delete"
     */
    public static final String TAG_DELETE = "servicesGateway-Delete";
    
    /**
     * "servicesGateway-Count"
     */
    public static final String TAG_COUNT = "servicesGateway-Count";
   
    /**
     * "servicesGateway-Tris"
     */
    public static final String TAG_TRIS = "servicesGateway-Tris";
    
    /**
     * "servicesGateway-Dedoublonnage"
     */
    public static final String TAG_DEDOUBLONNAGE 
    	= "servicesGateway-Dedoublonnage";

    /**
     * "   "
     */
    public static final String BLANK = "   ";
    
    /**
     * "vêtement"
     */
    public static final String VETEMENT = "vêtement";
    
    /**
     * "outillage"
     */
    public static final String OUTILLAGE = "outillage";
   
    /**
     * "camping"
     */
    public static final String CAMPING = "camping";
    
    /**
     * "me"
     */
    public static final String RECHERCHE_ME = "me";
   
    /**
     * "boom"
     */
    public static final String MSG_BOOM = "boom";
    
    /**
     * "typeProduit"
     */
    public static final String PROP_TYPEPRODUIT = "typeProduit";
    
    /**
     * "idTypeProduit"
     */
    public static final String PROP_IDTYPEPRODUIT = "idTypeProduit";

    /** 1L */
    public static final Long ID_1 = Long.valueOf(1L);

    /** 2L */
    public static final Long ID_2 = Long.valueOf(2L);

    /** 3L */
    public static final Long ID_3 = Long.valueOf(3L);

    /** 4L */
    public static final Long ID_4 = Long.valueOf(4L);

    /** 999_999L */
    public static final Long ID_INEXISTANT = Long.valueOf(999_999L);

    /** 10L */
    public static final long TOTAL_10 = 10L;

    /** 0L */
    public static final long TOTAL_0 = 0L;

    /** 
     * "Erreur Technique lors du stockage : " 
     */
    public static final String MSG_PREFIX_ERREUR_TECH
        = TypeProduitGatewayIService.ERREUR_TECHNIQUE_STOCKAGE;

    /** 
     * "Erreur Technique - Le stockage a retourné null." 
     */
    public static final String MSG_ERREUR_TECH_KO_STOCKAGE
        = TypeProduitGatewayIService.ERREUR_TECHNIQUE_KO_STOCKAGE;

    // *************************** ATTRIBUTS *******************************/

    /**
     * <div>
     * <p>Mock du DAO pour l'objet métier 
     * {@link TypeProduit}.</p>
     * </div>
     */
    @Mock
    private TypeProduitDaoJPA typeProduitDaoJPA;

    /**
     * <div>
     * <p>Service testé.</p>
     * </div>
     */
    private TypeProduitGatewayJPAService service;

    // ************************* CONSTRUCTEUR ****************************/

    /**
     * <div>
     * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
     * </div>
     */
    public TypeProduitGatewayJPAServiceMockTest() {
        super();
    } // __________________________________________________________________
    
    

    // *************************** INIT **********************************/

    /**
     * <div>
     * <p>Initialise le service GATEWAY avec un DAO mocké 
     * avant chaque test.</p>
     * </div>
     */
    @BeforeEach
    public void init() {
    	
        this.service 
        	= new TypeProduitGatewayJPAService(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________

    
    
    // ************************* METHODES ********************************/
    
    
    
    // =============================== CREER ==============================

    
    
    /**
     * <div>
     * <p>garantit que creer(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull}</li>
     * <li>émet un
     * {@link TypeProduitGatewayIService#MESSAGE_CREER_KO_PARAM_NULL}</li>
     * <li>n'appelle pas le DAO</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(null) : jette ExceptionAppliParamNull et n'appelle pas le DAO")
    @Test
    public void testCreerNull() {
    	
    	/* ARRANGE - ACT - ASSERT */
    	/* Garantit que this.service.creer(null)
    	 * - jette une ExceptionAppliParamNull
    	 * - émet un message MESSAGE_CREER_KO_PARAM_NULL 
    	 * (message contractuel du port).
    	 */
        assertThatThrownBy(() -> this.service.creer(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_CREER_KO_PARAM_NULL);
        
        /* Garantit que le DAO mocké n'a pas été appelé. */
        /* - verify(..., never()).méthode(...) = preuve ciblée 
         * sur une méthode critique précise.
         * - verifyNoInteractions(mock) = preuve globale 
         * que le mock entier n'a pas été touché.*/
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que creer(libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank}</li>
     * <li>émet un
     * {@link TypeProduitGatewayIService#MESSAGE_CREER_KO_LIBELLE_BLANK}</li>
     * <li>n'appelle pas le DAO</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(libellé blank) : jette ExceptionAppliLibelleBlank et n'appelle pas le DAO")
    @Test
    public void testCreerLibelleBlank() {
    	
    	/* ARRANGE */
        final TypeProduit metier = new TypeProduit(BLANK);
        
        /* ACT - ASSERT */
        /* Garantit que this.service.creer(metier)
         * - jette une ExceptionAppliLibelleBlank
         * - émet un message MESSAGE_CREER_KO_LIBELLE_BLANK.
         */
        assertThatThrownBy(() -> this.service.creer(metier))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_CREER_KO_LIBELLE_BLANK);
        
        /* Garantit que le DAO mocké n'a pas été appelé. */
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
 
    
    
    /**
     * <div>
     * <p>garantit que creer(DAO.save(...) retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet le message
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE}</li>
     * <li>appelle le DAO une fois</li>
     * <li>tente bien de persister une entité JPA</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(DAO.save(...) retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testCreerSaveRetourneNull() {
    	
    	/* ARRANGE :
    	 * prépare un objet métier valide pour atteindre réellement l'appel DAO.
    	 * On utilise un objet métier stockable,
    	 * afin que l'échec observé provienne bien du stockage
    	 * et non d'un contrôle préalable sur les paramètres.
    	 */
        final TypeProduit aCreer = fabriquerTypeProduit(VETEMENT, null);
        
        /* 
         * Configuration du Mock :
         * Simule un DAO qui retourne null au lieu de renvoyer l'entité sauvée :
         * ce comportement doit être interprété comme un échec technique de stockage.
         * Le contrat de creer(...) impose alors :
         * - une ExceptionTechniqueGateway
         * - avec le message ERREUR_TECHNIQUE_KO_STOCKAGE.
         */
        when(this.typeProduitDaoJPA.save(any(TypeProduitJPA.class)))
            .thenReturn(null);

        /* ACT - ASSERT :
         * garantit que this.service.creer(aCreer)
         * - jette une ExceptionTechniqueGateway
         * - émet exactement le message ERREUR_TECHNIQUE_KO_STOCKAGE.
         */
        assertThatThrownBy(() -> this.service.creer(aCreer))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);
        
        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec une entité JPA à persister.
         * Cela prouve que le service a bien tenté l'accès au stockage
         * avant de constater l'anomalie technique "retour null".
         */
        verify(this.typeProduitDaoJPA, times(1))
            .save(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que si le DAO jette une exception technique pendant creer(...) :</p>
     * <ul>
     * <li>le SERVICE GATEWAY JPA jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(KO DAO message non nul) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testCreerDaoSaveExceptionMessageNonNull() {

        /* ARRANGE :
         * prépare un objet métier valide pour atteindre réellement l'appel DAO.
         */
        final TypeProduit aCreer = fabriquerTypeProduit(VETEMENT, null);

        /* Prépare une panne technique du DAO avec message non nul.
         * Ce cas doit prouver simultanément :
         * - la requalification en ExceptionTechniqueGateway ;
         * - la conservation du message technique ;
         * - la propagation de la cause d'origine.
         */
        final RuntimeException causeDao = new RuntimeException(MSG_BOOM);

        /* 
         * Configuration du Mock : 
         * typeProduitDaoJPA.save(...) -> jette une Exception causeDao
         */
        when(this.typeProduitDaoJPA.save(any(TypeProduitJPA.class)))
            .thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois this.service.creer(aCreer)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.creer(aCreer));

        /* ASSERT :
         * garantit que this.service.creer(aCreer)
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

        /* Garantit que le DAO mocké a bien été appelé une fois. */
        verify(this.typeProduitDaoJPA).save(any(TypeProduitJPA.class));

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que si le DAO jette une exception technique avec message null pendant creer(...) :</p>
     * <ul>
     * <li>le SERVICE GATEWAY JPA jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non nul dérivé de l'exception technique</li>
     * <li>conserve comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(KO DAO message null) : jette ExceptionTechniqueGateway avec message sûr non nul")
    @Test
    public void testCreerDaoSaveExceptionMessageNull() {
    	
    	/* ARRANGE :
    	 * prépare un objet métier valide pour atteindre réellement l'appel DAO.
    	 */
    	final TypeProduit aCreer = fabriquerTypeProduit(VETEMENT, null);
    	
    	/* Prépare une exception technique dont le message est null.
    	 * Ce cas est utile pour vérifier que safeMessage(e)
    	 * construit malgré tout un message sûr non nul.
    	 */
    	final RuntimeException causeDao = new RuntimeException((String) null);
    	
    	/* 
    	 * Configuration du Mock : 
    	 * Simule une panne technique du DAO au moment du save(...).
    	 * Le DAO mocké jette ici une RuntimeException sans message.
    	 */
    	when(this.typeProduitDaoJPA.save(any(TypeProduitJPA.class)))
    		.thenThrow(causeDao);
    	
    	/* ACT :
    	 * exécute une seule fois this.service.creer(aCreer)
    	 * et capture l'exception réellement levée,
    	 * afin de contrôler ensuite son type, son message et sa cause.
    	 */
    	final Throwable throwable
    		= org.assertj.core.api.Assertions.catchThrowable(
    				() -> this.service.creer(aCreer));
    	
    	/* ASSERT :
    	 * garantit que this.service.creer(aCreer)
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
    	
    	/* Garantit que le DAO mocké a bien été appelé une fois. */
    	verify(this.typeProduitDaoJPA).save(any(TypeProduitJPA.class));
    	
    } // __________________________________________________________________

    
    
    /**
	 * <div>
	 * <p>garantit que si le stockage refuse creer(...) pour cause de doublon fonctionnel :</p>
	 * <ul>
	 * <li>le SERVICE GATEWAY JPA jette une {@link ExceptionTechniqueGateway}</li>
	 * <li>émet un message commençant par
	 * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
	 * <li>conserve un message technique sûr dérivé de l'exception de stockage</li>
	 * <li>propage l'exception technique cause</li>
	 * <li>appelle le DAO une fois</li>
	 * </ul>
	 * </div>
	 */
	@Tag(TAG_CREER)
	@DisplayName("creer(doublon) : jette ExceptionTechniqueGateway et propage la cause")
	@Test
	public void testCreerDoublon() {
		
		/* ARRANGE :
		 * prépare un objet métier valide.
		 * Le doublon n'est pas simulé au niveau métier,
		 * mais au niveau du stockage au moment du save(...).
		 * On utilise un libellé valide pour atteindre réellement l'appel DAO.
		 */
		final TypeProduit aCreer = fabriquerTypeProduit(VETEMENT, null);
		
		/* Prépare l'exception technique d'intégrité levée par le stockage :
		 * elle représente ici un refus de création pour cause de doublon fonctionnel.
		 * Le message technique d'origine doit être conservé dans le message final
		 * de l'ExceptionTechniqueGateway.
		 */
		final String messageTechnique = "contrainte d'unicité violée";
		final DataIntegrityViolationException causeDao
			= new DataIntegrityViolationException(messageTechnique);
		
		/* 
		 * Configuration du Mock : 
		 * Simule un refus du stockage pour cause de doublon fonctionnel :
		 * le DAO mocké jette une exception technique d'intégrité au moment du save(...).
		 */
		when(this.typeProduitDaoJPA.save(any(TypeProduitJPA.class)))
			.thenThrow(causeDao);
		
		/* ACT :
		 * exécute une seule fois this.service.creer(aCreer)
		 * et capture l'exception réellement levée,
		 * afin de contrôler ensuite séparément :
		 * - le type de l'exception,
		 * - son message,
		 * - et sa cause technique d'origine.
		 */
		final Throwable throwable
			= org.assertj.core.api.Assertions.catchThrowable(
					() -> this.service.creer(aCreer));
		
		/* ASSERT :
		 * garantit que this.service.creer(aCreer)
		 * - jette une ExceptionTechniqueGateway
		 * - émet un message commençant par ERREUR_TECHNIQUE_STOCKAGE
		 * - conserve le message technique d'origine
		 * - propage l'exception technique cause.
		 */
		assertThat(throwable)
			.isInstanceOf(ExceptionTechniqueGateway.class)
			.hasMessageContaining(TypeProduitGatewayIService.ERREUR_TECHNIQUE_STOCKAGE)
			.hasMessageContaining(messageTechnique);
		
		/* Garantit que la cause technique d'origine
		 * est bien propagée par l'ExceptionTechniqueGateway.
		 */
		assertThat(throwable.getCause()).isSameAs(causeDao);
		
		/* Garantit que le DAO mocké a bien été appelé une fois.
		 * Cela prouve que l'échec observé provient bien du stockage
		 * au moment de la tentative de persistance.
		 */
		verify(this.typeProduitDaoJPA).save(any(TypeProduitJPA.class));
		
	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que creer(OK) :</p>
	 * <ul>
	 * <li>appelle le DAO pour persister un {@link TypeProduitJPA}</li>
	 * <li>envoie au DAO une entité JPA sans identifiant initial</li>
	 * <li>envoie au DAO une entité JPA portant le bon libellé métier</li>
	 * <li>retourne un {@link TypeProduit} persistant</li>
	 * <li>retourne un objet métier portant l'identifiant généré par le stockage</li>
	 * <li>retourne un objet métier portant le bon libellé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_CREER)
	@DisplayName("creer(OK) : persiste via le DAO et retourne l'objet métier persisté")
	@Test
	public void testCreerNominal() throws Exception {
		
		/* ARRANGE :
		 * prépare un objet métier valide à créer,
		 * une entité JPA simulant le retour du stockage après save(...),
		 * et un captor pour contrôler précisément ce qui a été envoyé au DAO.
		 */
	    final TypeProduit aCreer = fabriquerTypeProduit(VETEMENT, null);
	    final TypeProduitJPA persistee = fabriquerTypeProduitJPA(VETEMENT, ID_1);
	    final ArgumentCaptor<TypeProduitJPA> captor
	    	= ArgumentCaptor.forClass(TypeProduitJPA.class);
	
	    /* 
	     * Configuration du Mock : 
	     * Simule un DAO qui persiste correctement l'entité
	     * et retourne une entité JPA portant l'identifiant généré ID_1.
	     */
	    when(this.typeProduitDaoJPA.save(any(TypeProduitJPA.class)))
	        .thenReturn(persistee);
	
	    /* ACT :
	     * exécute la création métier via le service GATEWAY JPA.
	     */
	    final TypeProduit resultat = this.service.creer(aCreer);
	
	    /* ASSERT :
	     * garantit que le DAO mocké a bien été appelé
	     * avec une entité JPA construite à partir de l'objet métier d'entrée.
	     */
	    verify(this.typeProduitDaoJPA).save(captor.capture());
	
	    /* Garantit que l'entité envoyée au DAO :
	     * - n'est pas null
	     * - ne porte pas encore d'identifiant
	     * - porte bien le libellé métier attendu.
	     */
	    final TypeProduitJPA envoyeAuDAO = captor.getValue();
	    assertThat(envoyeAuDAO).isNotNull();
	    assertThat(envoyeAuDAO.getIdTypeProduit()).isNull();
	    assertThat(envoyeAuDAO.getTypeProduit()).isEqualTo(VETEMENT);
	
	    /* Garantit que l'objet métier retourné par le service :
	     * - n'est pas null
	     * - porte l'identifiant généré par le stockage
	     * - porte le bon libellé métier.
	     */
	    assertThat(resultat).isNotNull();
	    assertThat(resultat.getIdTypeProduit()).isEqualTo(ID_1);
	    assertThat(resultat.getTypeProduit()).isEqualTo(VETEMENT);
	    
	} // __________________________________________________________________
    
    

    // ======================== RechercherTous ============================



	/**
     * <div>
     * <p>garantit que si DAO.findAll() retourne null :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet le message
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE}</li>
     * <li>appelle une seule fois le DAO mocké avec Mockito.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous(DAO.findAll() retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testRechercherTousDAORetourneNull() {
    	
    	/* ARRANGE :
    	 * Configuration du Mock : 
    	 * simule un DAO qui retourne null au lieu d'une liste.
    	 * Ce comportement doit être interprété comme 
    	 * un échec technique de stockage.
    	 */
        when(this.typeProduitDaoJPA.findAll()).thenReturn(null);
        
        /* ACT - ASSERT :
         * garantit que this.service.rechercherTous()
         * - jette une ExceptionTechniqueGateway
         * - émet exactement le message ERREUR_TECHNIQUE_KO_STOCKAGE.
         */
        assertThatThrownBy(() -> this.service.rechercherTous())
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);
        
        /* Garantit que le DAO mocké a bien été appelé une fois. */
        verify(this.typeProduitDaoJPA).findAll();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que si DAO.findAll() retourne une liste vide :</p>
     * <ul>
     * <li>retourne une {@link List} non null</li>
     * <li>retourne une liste vide</li>
     * <li>ne jette aucune exception</li>
     * <li>appelle le DAO une fois</li>
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
    	 * Configuration du Mock : 
    	 * simule un stockage vide.
    	 * Le DAO retourne ici une liste vide,
    	 * et non null.
    	 */
        when(this.typeProduitDaoJPA.findAll())
        	.thenReturn(Collections.emptyList());
        
        /* ACT :
         * exécute la recherche complète via le service GATEWAY JPA.
         */
        final List<TypeProduit> resultat = this.service.rechercherTous();
        
        /* ASSERT :
         * garantit que this.service.rechercherTous()
         * - retourne une liste non null
         * - retourne une liste vide
         * - ne jette pas d'exception.
         */
        assertThat(resultat).isNotNull().isEmpty();
        
        /* 
         * Garantit que le DAO mocké a bien été appelé une fois.
         * findAll() précise quelle méthode du mock a été appelée.
         */
        verify(this.typeProduitDaoJPA).findAll();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que si DAO.findAll() jette une exception technique 
     * avec message non nul :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine de l'exception</li>
     * <li>propage l'exception technique cause</li>
     * <li>appelle le DAO une fois via {@code findAll()}</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous(KO DAO message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testRechercherTousDaoExceptionMessageNonNull() {

    	/* ARRANGE :
    	 * Configuration du Mock : 
    	 * simule un DAO qui jette une exception technique avec 
    	 * message non nul au moment de l'accès au stockage via findAll().
    	 * Ce cas doit être encapsulé par le service dans une 
    	 * ExceptionTechniqueGateway.
    	 */
        final RuntimeException causeDao
            = new RuntimeException(MSG_BOOM);

        when(this.typeProduitDaoJPA.findAll())
            .thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois this.service.rechercherTous()
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite :
         * - son type,
         * - son message,
         * - et sa cause technique d'origine.
         */
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.rechercherTous());

        /* ASSERT :
         * garantit que this.service.rechercherTous()
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
         * via la méthode findAll().
         */
        verify(this.typeProduitDaoJPA).findAll();

    } // _________________________________________________________________    
    

    
    /**
     * <div>
     * <p>garantit que si DAO.findAll() jette une exception technique 
     * avec message null :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non nul dérivé de l'exception technique</li>
     * <li>propage l'exception technique cause</li>
     * <li>appelle le DAO une fois via {@code findAll()}</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous(KO DAO message null) : jette ExceptionTechniqueGateway avec message sûr non nul")
    @Test
    public void testRechercherTousDaoExceptionMessageNull() {

    	/* ARRANGE :
    	 * Configuration du Mock : 
    	 * simule un DAO qui jette une exception technique sans message
    	 * au moment de l'accès au stockage via findAll().
    	 * Ce cas est utile pour vérifier que safeMessage(e)
    	 * construit malgré tout un message sûr non nul.
    	 */
        final RuntimeException causeDao
            = new RuntimeException((String) null);

        when(this.typeProduitDaoJPA.findAll())
            .thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois this.service.rechercherTous()
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite :
         * - son type,
         * - son message,
         * - et sa cause technique d'origine.
         */
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.rechercherTous());

        /* ASSERT :
         * garantit que this.service.rechercherTous()
         * - jette une ExceptionTechniqueGateway
         * - émet un message commençant par ERREUR_TECHNIQUE_STOCKAGE
         * - n'émet pas un message null
         * - utilise un texte sûr dérivé de l'exception technique.
         *
         * Avec l'implémentation actuelle de safeMessage(e),
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

        /* Garantit que le DAO mocké a bien été appelé une fois
         * via la méthode findAll().
         */
        verify(this.typeProduitDaoJPA).findAll();

    } // _________________________________________________________________
    
    
    
	/**
	 * <div>
	 * <p>garantit que si DAO.findAll() retourne une liste
	 * contenant des nulls et deux libellés fonctionnellement identiques
	 * ne différant que par la casse :</p>
	 * <ul>
	 * <li>retourne une {@link List} non null</li>
	 * <li>filtre les éléments null</li>
	 * <li>dédoublonne les résultats au sens métier
	 * sans tenir compte de la casse</li>
	 * <li>trie les résultats par libellé</li>
	 * <li>appelle le DAO une fois</li>
	 * </ul>
	 * <p>Ce test prouve donc explicitement la règle contractuelle
	 * d'unicité métier sur le libellé,
	 * même lorsque le stockage contient deux valeurs
	 * ne différant que par la casse.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_RECHERCHER)
	@DisplayName("rechercherTous(nulls + doublons de casse) : filtre, dédoublonne sans tenir compte de la casse et trie")
	@Test
	public void testRechercherTousTriDedoublonnage() throws Exception {
	
	    /* ARRANGE :
	     * Configuration du Mock : simule un DAO qui retourne :
	     * - un élément null
	     * - deux objets fonctionnellement identiques
	     *   portant le même libellé à la casse près
	     * - plusieurs libellés non triés.
	     *
	     * Le service doit :
	     * - filtrer le null,
	     * - ne conserver qu'un seul "vêtement" au sens métier,
	     * - puis trier le résultat final.
	     */
	    final String vetementMajuscule = VETEMENT.toUpperCase(Locale.ROOT);
	
	    final List<TypeProduitJPA> contenu = Arrays.asList(
	            fabriquerTypeProduitJPA(OUTILLAGE, ID_2),
	            null,
	            fabriquerTypeProduitJPA(VETEMENT, ID_1),
	            fabriquerTypeProduitJPA(vetementMajuscule, ID_3),
	            fabriquerTypeProduitJPA(CAMPING, ID_4));
	
	    when(this.typeProduitDaoJPA.findAll()).thenReturn(contenu);
	
	    /* ACT :
	     * exécute la recherche complète via le service GATEWAY JPA.
	     */
	    final List<TypeProduit> resultat = this.service.rechercherTous();
	
	    /* ASSERT :
	     * garantit que this.service.rechercherTous()
	     * - retourne une liste non null,
	     * - filtre l'élément null,
	     * - ne conserve qu'une seule occurrence métier de "vêtement"
	     *   après normalisation de la casse,
	     * - trie les libellés par ordre alphabétique.
	     *
	     * La normalisation en lowerCase(Locale.ROOT) est volontaire :
	     * elle permet de prouver directement l'unicité métier
	     * sans imposer artificiellement la casse exacte
	     * conservée par l'implémentation.
	     */
	    assertThat(resultat).isNotNull().hasSize(3);
	    assertThat(resultat)
	        .extracting(typeProduit -> typeProduit.getTypeProduit().toLowerCase(Locale.ROOT))
	        .containsExactly(CAMPING, OUTILLAGE, VETEMENT);
	
	    /* Garantit que le DAO mocké a bien été appelé une fois
	     * via la méthode findAll().
	     */
	    verify(this.typeProduitDaoJPA).findAll();
	
	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>garantit que rechercherTous(nominal) :</p>
	 * <ul>
	 * <li>retourne une liste non null ;</li>
	 * <li>filtre les valeurs null ;</li>
	 * <li>dédoublonne les doublons fonctionnels ;</li>
	 * <li>retourne une liste triée ;</li>
	 * <li>appelle le DAO une fois via {@code findAll()}.</li>
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
		final TypeProduitJPA e1 = fabriquerTypeProduitJPA(OUTILLAGE, ID_2);
		final TypeProduitJPA e2 = fabriquerTypeProduitJPA(VETEMENT, ID_1);
		final TypeProduitJPA e3 = fabriquerTypeProduitJPA(VETEMENT, ID_3);
		final TypeProduitJPA e4 = fabriquerTypeProduitJPA(CAMPING, ID_4);

		final List<TypeProduitJPA> entities = new ArrayList<TypeProduitJPA>();
		entities.add(e1);
		entities.add(null);
		entities.add(e2);
		entities.add(e3);
		entities.add(e4);

		/* 
		 * Configuration du Mock : 
		 * L'appel typeProduitDaoJPA.findAll()
		 * sur le DAO objet métier mocké retourne la liste entities.
		 */
		when(this.typeProduitDaoJPA.findAll()).thenReturn(entities);

		/* ACT :
		 * sollicite la méthode service.rechercherTous()
		 * dans un scénario nominal complet.
		 */
		final List<TypeProduit> retour = this.service.rechercherTous();

		/* ASSERT :
		 * vérifie d'abord que la méthode retourne
		 * une liste exploitable.
		 */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(3);

		/* Vérifie ensuite que :
		 * - les valeurs null ont été filtrées ;
		 * - les doublons fonctionnels ont été supprimés ;
		 * - l'ordre final est trié par libellé.
		 */
		assertThat(retour)
			.extracting(TypeProduit::getTypeProduit)
			.containsExactly(CAMPING, OUTILLAGE, VETEMENT);

		/* 
		 * Vérifie que la méthode findAll()
		 * du DAO typeProduitDaoJPA mocké avec Mockito
		 * a bien été appelée une fois.
		 */
		verify(this.typeProduitDaoJPA).findAll();

	} // _________________________________________________________________
	
    
    
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
        final List<TypeProduitJPA> contenuJPA = Arrays.asList(
                fabriquerTypeProduitJPA(VETEMENT, ID_1),
                fabriquerTypeProduitJPA(OUTILLAGE, ID_2));

        final Page<TypeProduitJPA> page = creerPage(
                contenuJPA,
                RequetePage.PAGE_DEFAUT,
                RequetePage.TAILLE_DEFAUT,
                2L);

        /* 
         * Crée un ArgumentCaptor Mockito capable de capturer
         * l'argument de type Pageable réellement transmis au DAO.
         *
         * Ici, le test ne veut pas seulement vérifier que
         * typeProduitDaoJPA.findAll(...) a été appelé.
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
        when(this.typeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        /* ACT :
         * appelle le service avec une requête null.
         *
         * Le contrat impose alors :
         * - aucune Exception ;
         * - utilisation d'une pagination par défaut ;
         * - délégation au DAO avec un Pageable cohérent.
         */
        final ResultatPage<TypeProduit> resultat
            = this.service.rechercherTousParPage(null);

        /* ASSERT :
         * garantit d'abord que le service a bien appelé le DAO,
         * puis permet d'inspecter le Pageable réellement transmis.
         */
        verify(this.typeProduitDaoJPA).findAll(captor.capture());

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
            .extracting(TypeProduit::getTypeProduit)
            .containsExactly(VETEMENT, OUTILLAGE);

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
        when(this.typeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(null);

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
         * d'interroger le DAO via findAll(...).
         *
         * Le but de ce test n'est pas de contrôler le détail du Pageable,
         * mais de prouver la réaction contractuelle du service
         * quand le DAO répond null.
         */
        verify(this.typeProduitDaoJPA).findAll(any(Pageable.class));

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
        final Page<TypeProduitJPA> pageMock = mock(Page.class);
        when(pageMock.getContent()).thenReturn(null);

        /* Simule ensuite un DAO qui renvoie cette page incohérente
         * lorsque le service l'appelle en pagination.
         *
         * Le scénario préparé est donc :
         * DAO.findAll(...) ne renvoie pas null,
         * mais renvoie une Page inutilisable car son contenu est null.
         */
        when(this.typeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(pageMock);

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
         * garantit que le service a bien interrogé le DAO.
         *
         * Le cœur du test n'est pas ici le détail du Pageable,
         * mais la réaction contractuelle du service
         * face à une Page existante mais incohérente.
         */
        verify(this.typeProduitDaoJPA).findAll(any(Pageable.class));

    } // __________________________________________________________________
    
    
    
	/**
	 * <div>
	 * <p>garantit que si une erreur technique survient 
	 * pendant l'accès au DAO :</p>
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
	     * Configuration du Mock : simule une panne technique côté DAO.
	     *
	     * Le DAO ne renvoie ni page valide ni valeur null :
	     * il échoue brutalement en lançant une RuntimeException.
	     *
	     * Le message MSG_BOOM est volontairement non null,
	     * afin de prouver que le service l'intègre bien
	     * dans le message final sécurisé de l'ExceptionTechniqueGateway.
	     *
	     * La cause est stockée dans une variable dédiée
	     * pour pouvoir vérifier ensuite que l'ExceptionTechniqueGateway
	     * propage exactement cette instance,
	     * et pas seulement une RuntimeException quelconque.
	     */
	    final RuntimeException causeDao = new RuntimeException(MSG_BOOM);

	    when(this.typeProduitDaoJPA.findAll(any(Pageable.class)))
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
	        = org.assertj.core.api.Assertions.catchThrowable(
	                () -> this.service.rechercherTousParPage(new RequetePage()));

	    /* ASSERT :
	     * garantit que le service :
	     * - requalifie l'exception brute en ExceptionTechniqueGateway ;
	     * - conserve le préfixe technique attendu ;
	     * - conserve le message technique d'origine MSG_BOOM.
	     */
	    assertThat(throwable)
	        .isInstanceOf(ExceptionTechniqueGateway.class)
	        .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
	        .hasMessageContaining(MSG_BOOM);

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
	     * d'accéder au stockage via le DAO.
	     */
	    verify(this.typeProduitDaoJPA).findAll(any(Pageable.class));

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
	     * Configuration du Mock : simule une panne technique côté DAO
	     * dont le message d'origine vaut null.
	     *
	     * Ce cas est important car le contrat n'autorise jamais
	     * un message final null dans l'ExceptionTechniqueGateway :
	     * le service doit donc construire un message sûr.
	     */
	    final RuntimeException causeDao = new RuntimeException((String) null);
	
	    when(this.typeProduitDaoJPA.findAll(any(Pageable.class)))
	        .thenThrow(causeDao);
	
	    /* ACT :
	     * exécute une seule fois le service avec une requête valide
	     * afin d'isoler précisément le scénario testé :
	     * l'échec doit venir du stockage,
	     * pas du paramètre d'entrée.
	     *
	     * On capture ensuite l'exception réellement levée
	     * pour contrôler son type, son message et sa cause.
	     */
	    final Throwable throwable
	        = org.assertj.core.api.Assertions.catchThrowable(
	                () -> this.service.rechercherTousParPage(new RequetePage()));
	
	    /* ASSERT :
	     * garantit que le service :
	     * - requalifie l'exception brute en ExceptionTechniqueGateway ;
	     * - conserve le préfixe technique attendu ;
	     * - fabrique malgré tout un message sûr non nul.
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
	     * reste bien propagée.
	     */
	    assertThat(throwable.getCause()).isSameAs(causeDao);
	
	    /* Garantit que le service a bien tenté
	     * d'accéder au stockage via le DAO.
	     */
	    verify(this.typeProduitDaoJPA).findAll(any(Pageable.class));
	
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
         * prépare une requête métier dont l'appelant demande
         * explicitement une taille zéro.
         *
         * Le point important à verrouiller ici est le comportement réel
         * de l'entrée publique :
         * la RequetePage ne conserve pas 0,
         * elle le remplace par la taille par défaut.
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
        final List<TypeProduitJPA> contenuJPA = Arrays.asList(
                fabriquerTypeProduitJPA(VETEMENT, ID_1),
                fabriquerTypeProduitJPA(OUTILLAGE, ID_2));

        final Page<TypeProduitJPA> page = creerPage(
                contenuJPA,
                RequetePage.PAGE_DEFAUT,
                RequetePage.TAILLE_DEFAUT,
                2L);
        
        /* 
         * Crée un ArgumentCaptor Mockito capable de capturer
         * l'argument de type Pageable réellement transmis au DAO.
         *
         * Ici, le test ne veut pas seulement vérifier que
         * typeProduitDaoJPA.findAll(...) a été appelé.
         * Il veut aussi inspecter le Pageable construit par le service :
         * - numéro de page ;
         * - taille de page ;
         * - présence ou absence de tri.
         *
         * ArgumentCaptor.forClass(Pageable.class) indique donc à Mockito
         * quel type d'argument devra être capturé lors du verify(...).
         * 
         * Le captor est indispensable ici :
         * sans lui, on pourrait seulement vérifier que findAll(...) 
         * a été appelé ;
         * avec lui, on peut contrôler concrètement
         * le numéro de page, la taille et l'absence de tri.
         */
        final ArgumentCaptor<Pageable> captor
            = ArgumentCaptor.forClass(Pageable.class);

        /* Configuration du Mock : Simule un DAO qui renvoie une page valide
         * quel que soit le Pageable reçu.
         *
         * Le but n'est pas de tester Spring Data,
         * mais de verrouiller ce que le service envoie réellement
         * au DAO après normalisation de la requête.
         */
        when(this.typeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        /* ACT :
         * sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         *
         * Le scénario prouve ici qu'une demande initiale pageSize == 0
         * n'entraîne pas d'Exception
         * et aboutit à une pagination exploitable.
         */
        final ResultatPage<TypeProduit> resultat
            = this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * garantit d'abord que le DAO mocké avec Mockito
         * a bien été interrogé,
         * puis permet d'inspecter le Pageable réellement transmis.
         */
        verify(this.typeProduitDaoJPA).findAll(captor.capture());

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
            .extracting(TypeProduit::getTypeProduit)
            .containsExactly(VETEMENT, OUTILLAGE);

    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que si une {@link RequetePage} contient des consignes de tri :</p>
     * <ul>
     * <li>le service convertit les consignes de tri valides en {@link Sort} Spring ;</li>
     * <li>il ignore les consignes de tri inutilisables
     * (entrée {@code null}, propriété vide) ;</li>
     * <li>il transmet au DAO un {@link Pageable} cohérent
     * avec la page, la taille et le tri demandés ;</li>
     * <li>il retourne enfin un {@link ResultatPage} cohérent
     * avec la page renvoyée par le DAO.</li>
     * </ul>
     * <p>Ce test prouve donc à la fois :</p>
     * <ul>
     * <li>la conversion technique de {@link TriSpec} vers {@link Sort} ;</li>
     * <li>le filtrage des tris invalides avant l'appel DAO ;</li>
     * <li>la cohérence du résultat métier restitué par le service.</li>
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
        final List<TriSpec> tris = new ArrayList<TriSpec>();
        tris.add(null);
        tris.add(new TriSpec(BLANK, DirectionTri.ASC));
        tris.add(new TriSpec(PROP_TYPEPRODUIT, DirectionTri.ASC));
        tris.add(new TriSpec(PROP_IDTYPEPRODUIT, DirectionTri.DESC));

        final RequetePage requete = new RequetePage(1, 3, tris);

        final Page<TypeProduitJPA> page = creerPage(
                Collections.singletonList(fabriquerTypeProduitJPA(CAMPING, ID_4)),
                1,
                3,
                TOTAL_10);

        /*
         * Crée un ArgumentCaptor Mockito capable de capturer
         * l'argument de type Pageable réellement transmis au DAO.
         *
         * Ici, le test ne veut pas seulement vérifier que
         * typeProduitDaoJPA.findAll(...) a été appelé.
         * Il veut aussi inspecter le Pageable construit par le service :
         * - numéro de page ;
         * - taille de page ;
         * - tri réellement transmis au stockage.
         */
        final ArgumentCaptor<Pageable> captor
            = ArgumentCaptor.forClass(Pageable.class);

        /* 
         * Configuration du Mock : 
         * Simule un DAO qui renvoie une page techniquement valide
         * quel que soit le Pageable reçu.
         *
         * Le but du test n'est pas d'éprouver le DAO,
         * mais de vérifier la manière dont le service
         * construit l'appel DAO puis reconstruit le résultat métier.
         */
        when(this.typeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        /* ACT :
         * appelle le service avec une requête paginée
         * qui contient volontairement des tris invalides et valides.
         *
         * Le test prouve ainsi que le service
         * ne recopie pas aveuglément la liste des tris reçus :
         * il la nettoie puis construit un Pageable exploitable par Spring Data.
         */
        final ResultatPage<TypeProduit> resultat
            = this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * garantit d'abord que le DAO a bien été appelé,
         * puis permet d'inspecter le Pageable transmis.
         *
         * C'est le cœur de la preuve :
         * on ne vérifie pas seulement que le service fonctionne,
         * on vérifie précisément ce qu'il envoie au stockage.
         */
        verify(this.typeProduitDaoJPA).findAll(captor.capture());

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
         *
         * La vérification ne se limite pas à constater
         * que les deux tris valides sont présents :
         * elle prouve aussi que le tri final contient exactement
         * les deux propriétés valides attendues.
         *
         * Ainsi :
         * - le TriSpec null est ignoré ;
         * - le TriSpec dont la propriété vaut BLANK est ignoré ;
         * - seuls PROP_TYPEPRODUIT et PROP_IDTYPEPRODUIT
         *   sont transmis au stockage.
         */
        final Sort sort = pageable.getSort();

        assertThat(sort.isSorted()).isTrue();

        assertThat(sort)
            .extracting(Sort.Order::getProperty)
            .containsExactly(PROP_TYPEPRODUIT, PROP_IDTYPEPRODUIT);

        assertThat(sort.getOrderFor(BLANK)).isNull();

        assertThat(sort.getOrderFor(PROP_TYPEPRODUIT)).isNotNull();
        assertThat(sort.getOrderFor(PROP_TYPEPRODUIT).getDirection())
            .isEqualTo(Sort.Direction.ASC);

        assertThat(sort.getOrderFor(PROP_IDTYPEPRODUIT)).isNotNull();
        assertThat(sort.getOrderFor(PROP_IDTYPEPRODUIT).getDirection())
            .isEqualTo(Sort.Direction.DESC);

        /* Garantit que le service restitue ensuite
         * un ResultatPage métier cohérent
         * avec la page DAO reçue en retour.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getPageNumber()).isEqualTo(1);
        assertThat(resultat.getPageSize()).isEqualTo(3);
        assertThat(resultat.getTotalElements()).isEqualTo(TOTAL_10);
        assertThat(resultat.getContent()).hasSize(1);
        assertThat(resultat.getContent().get(0).getTypeProduit()).isEqualTo(CAMPING);

    } // __________________________________________________________________
    
    
        
    /**
     * <div>
     * <p>garantit que si la page renvoyée par le DAO contient
     * des éléments {@code null} au milieu d'Entities {@link TypeProduitJPA}
     * valides :</p>
     * <ul>
     * <li>le service ne propage pas ces {@code null}
     * dans le contenu métier retourné ;</li>
     * <li>il conserve uniquement les {@link TypeProduitJPA}
     * effectivement convertissables ;</li>
     * <li>il retourne un contenu métier non null,
     * sans élément {@code null} parasite ;</li>
     * <li>il conserve les métadonnées de pagination
     * portées par la {@link Page} renvoyée par le DAO.</li>
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
         * - un TypeProduitJPA valide ;
         * - un élément null ;
         * - un autre TypeProduitJPA valide.
         */
        final List<TypeProduitJPA> contenuJPA = Arrays.asList(
                fabriquerTypeProduitJPA(VETEMENT, ID_1),
                null,
                fabriquerTypeProduitJPA(OUTILLAGE, ID_2));

        final Page<TypeProduitJPA> page
            = creerPage(
                    contenuJPA,
                    RequetePage.PAGE_DEFAUT,
                    RequetePage.TAILLE_DEFAUT,
                    contenuJPA.size());

        /*
         * Crée un ArgumentCaptor Mockito capable de capturer
         * le Pageable transmis au DAO objet métier.
         */
        final ArgumentCaptor<Pageable> captor
            = ArgumentCaptor.forClass(Pageable.class);

        /*
         * Configuration du Mock :
         * L'appel typeProduitDaoJPA.findAll(Pageable)
         * sur le DAO objet métier mocké retourne la page préparée.
         */
        when(this.typeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        /* ACT :
         * appelle le service avec une RequetePage présente,
         * instanciée avec le constructeur d'arité nulle.
         */
        final ResultatPage<TypeProduit> resultat
            = this.service.rechercherTousParPage(new RequetePage());

        /* ASSERT :
         * capture le Pageable transmis au DAO objet métier.
         */
        verify(this.typeProduitDaoJPA, times(1)).findAll(captor.capture());

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

        /* Vérifie que le contenu métier retourné
         * ne contient pas le null présent dans la Page DAO.
         */
        assertThat(resultat.getContent()).isNotNull();
        assertThat(resultat.getContent()).doesNotContainNull();
        assertThat(resultat.getContent()).hasSize(2);
        assertThat(resultat.getContent())
            .extracting(TypeProduit::getTypeProduit)
            .containsExactly(VETEMENT, OUTILLAGE);

    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que rechercherTousParPage(retourne page vide) :</p>
     * <ul>
     * <li>retourne un {@link ResultatPage} non null ;</li>
     * <li>retourne un contenu non null ;</li>
     * <li>retourne un contenu vide ;</li>
     * <li>retourne des métadonnées cohérentes avec la page vide
     * renvoyée par le stockage ;</li>
     * <li>appelle une seule fois la méthode findAll(Pageable)
     * du DAO mocké avec Mockito.</li>
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
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(retourne page vide) - retourne une page vide cohérente")
    @Test
    public void testRechercherTousParPagePageVide() throws Exception {

        /* ARRANGE :
         * prépare une Page Spring non null,
         * techniquement exploitable,
         * mais dont le contenu métier est vide.
         *
         * Ce scénario ne représente pas une anomalie du stockage :
         * le DAO répond correctement avec une page existante,
         * contenant simplement zéro élément.
         */
        final Page<TypeProduitJPA> page
            = creerPage(
                    Collections.emptyList(),
                    RequetePage.PAGE_DEFAUT,
                    RequetePage.TAILLE_DEFAUT,
                    TOTAL_0);

        /* 
         * Configuration du Mock :
         * Simule un DAO qui renvoie une page vide valide
         * lors de l'appel paginé.
         */
        when(this.typeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        /* ACT :
         * appelle le service avec une requête neutre.
         *
         * Le service doit accepter ce retour du stockage
         * et le convertir en ResultatPage métier vide,
         * sans jeter d'ExceptionTechniqueGateway.
         */
        final ResultatPage<TypeProduit> resultat
            = this.service.rechercherTousParPage(new RequetePage());

        /* ASSERT :
         * garantit que le DAO mocké avec Mockito
         * a bien été interrogé une fois via findAll(Pageable).
         */
        verify(this.typeProduitDaoJPA).findAll(any(Pageable.class));

        /* Garantit que la page métier retournée est exploitable :
         * elle existe, son contenu existe,
         * mais ce contenu est vide.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getContent()).isNotNull().isEmpty();

        /* Garantit que les métadonnées de pagination
         * restent cohérentes avec la page vide renvoyée
         * par le stockage.
         */
        assertThat(resultat.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(resultat.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(resultat.getTotalElements()).isEqualTo(TOTAL_0);

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
        final List<TypeProduitJPA> contenuJPA = Arrays.asList(
                fabriquerTypeProduitJPA(VETEMENT, ID_1),
                fabriquerTypeProduitJPA(OUTILLAGE, ID_2));

        final Page<TypeProduitJPA> page = creerPage(
                contenuJPA,
                RequetePage.PAGE_DEFAUT,
                RequetePage.TAILLE_DEFAUT,
                2L);

        /*
         * Crée un ArgumentCaptor Mockito capable de capturer
         * le Pageable transmis au DAO objet métier.
         */
        final ArgumentCaptor<Pageable> captor
            = ArgumentCaptor.forClass(Pageable.class);

        /*
         * Configuration du Mock :
         * L'appel typeProduitDaoJPA.findAll(Pageable)
         * sur le DAO objet métier mocké retourne la page préparée.
         */
        when(this.typeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        /* ACT :
         * Appelle le service avec new RequetePage().
         */
        final ResultatPage<TypeProduit> resultat
            = this.service.rechercherTousParPage(requete);

        /* ASSERT :
         * Capture le Pageable transmis au DAO objet métier.
         */
        verify(this.typeProduitDaoJPA, times(1)).findAll(captor.capture());

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

        /* Vérifie le contenu TypeProduit retourné.
         */
        assertThat(resultat.getContent()).isNotNull();
        assertThat(resultat.getContent())
            .extracting(TypeProduit::getTypeProduit)
            .containsExactly(VETEMENT, OUTILLAGE);

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
	 * <li>appelle une seule fois la méthode findAll(Pageable)
	 * du DAO mocké avec Mockito.</li>
	 * </ul>
	 * <p>Ce test complète les scénarios précédents :</p>
	 * <ul>
	 * <li>{@code rechercherTousParPage(null)} prouve le remplacement
	 * d'une requête absente ;</li>
	 * <li>{@code rechercherTousParPage(new RequetePage())} prouve
	 * la conversion d'une requête présente mais neutre ;</li>
	 * <li>ce test prouve le cas nominal avec une pagination
	 * explicitement demandée par l'appelant.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_PAGINATION)
	@DisplayName("rechercherTousParPage(OK) : garantit le bon fonctionnement de la pagination")
	@Test
	public void testRechercherTousParPageNominal() throws Exception {
		
	    /* ARRANGE :
	     * prépare une requête paginée explicite.
	     *
	     * Contrairement au test "requête neutre",
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
	    
	    /* Prépare une page DAO techniquement valide
	     * correspondant à la pagination explicitement demandée.
	     *
	     * Le contenu est volontairement simple et propre :
	     * ce test ne porte ni sur le filtrage des nulls,
	     * ni sur le tri,
	     * ni sur le dédoublonnage.
	     * Ces comportements sont déjà prouvés par d'autres tests.
	     */
	    final List<TypeProduitJPA> contenuJPA = Arrays.asList(
	            fabriquerTypeProduitJPA(CAMPING, ID_4),
	            fabriquerTypeProduitJPA(OUTILLAGE, ID_2));
	    
	    final Page<TypeProduitJPA> page = creerPage(
	            contenuJPA,
	            1,
	            2,
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
	    when(this.typeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);
	    
	    /* ACT :
	     * appelle le service avec une requête paginée explicite.
	     *
	     * Le service doit convertir cette requête en Pageable,
	     * interroger le DAO,
	     * puis reconstruire un ResultatPage métier cohérent.
	     */
	    final ResultatPage<TypeProduit> resultat
	        = this.service.rechercherTousParPage(requete);
	    
	    /* ASSERT :
	     * garantit d'abord que le DAO mocké avec Mockito
	     * a bien été interrogé une fois,
	     * puis récupère le Pageable réellement transmis.
	     */
	    verify(this.typeProduitDaoJPA).findAll(captor.capture());
	    
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
	        .extracting(TypeProduit::getTypeProduit)
	        .containsExactly(CAMPING, OUTILLAGE);
	    
	} // __________________________________________________________________
	

    
    // ======================== findByObjetMetier =========================

    
    
    /**
     * <div>
     * <p>garantit que findByObjetMetier(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull} ;</li>
     * <li>émet le message
     * {@link TypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL} ;</li>
     * <li>n'appelle pas le DAO.</li>
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
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL);

        /* Garantit que le DAO mocké n'a pas été appelé. */
        /* - verify(..., never()).méthode(...) = preuve ciblée 
         * sur une méthode critique précise.
         * - verifyNoInteractions(mock) = preuve globale 
         * que le mock entier n'a pas été touché. */
        verifyNoInteractions(this.typeProduitDaoJPA);

    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que findByObjetMetier(libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank} ;</li>
     * <li>émet le message
     * {@link TypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK} ;</li>
     * <li>n'appelle pas le DAO.</li>
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
        final TypeProduit metier = fabriquerTypeProduit(BLANK, null);

        /* ACT - ASSERT :
         * vérifie que l'appel avec un libellé blank
         * jette une ExceptionAppliLibelleBlank
         * avec le message MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK
         * (contrat du port).
         */
        assertThatThrownBy(() -> this.service.findByObjetMetier(metier))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);

        /* Garantit que le DAO mocké n'a pas été appelé. */
        /* - verify(..., never()).méthode(...) = preuve ciblée 
         * sur une méthode critique précise.
         * - verifyNoInteractions(mock) = preuve globale 
         * que le mock entier n'a pas été touché. */
        verifyNoInteractions(this.typeProduitDaoJPA);

    } // __________________________________________________________________
    
        

    /**
     * <div>
     * <p>garantit que findByObjetMetier(KO DAO message non null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>conserve le message technique d'origine du DAO ;</li>
     * <li>propage comme cause l'exception technique d'origine ;</li>
     * <li>appelle le DAO une fois avec le bon libellé métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(KO DAO message non null) - jette ExceptionTechniqueGateway")
    @Test
    public void testFindByObjetMetierDAOExceptionMessageNonNull() {

        /* ARRANGE :
         * prépare un objet métier valide
         * afin d'atteindre réellement la recherche dans le stockage.
         */
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, null);

        /* Configure le DAO mocké avec Mockito
         * pour que findByTypeProduitIgnoreCase(VETEMENT)
         * jette une RuntimeException avec message non null.
         */
        final RuntimeException causeDao = new RuntimeException(MSG_BOOM);

        when(this.typeProduitDaoJPA.findByTypeProduitIgnoreCase(VETEMENT))
            .thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois service.findByObjetMetier(metier)
         * et capture l'exception réellement levée.
         */
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.findByObjetMetier(metier));

        /* ASSERT :
         * vérifie l'exception technique observable,
         * son message contractuel sécurisé
         * et le message technique d'origine.
         */
        assertThat(throwable)
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);

        /* Vérifie que la cause technique d'origine
         * est bien propagée par l'ExceptionTechniqueGateway.
         */
        assertThat(throwable.getCause()).isSameAs(causeDao);

        /*
         * Vérifie ensuite les interactions réelles
         * avec la dépendance mockée.
         * Assure que le DAO objet métier a été appelé une fois
         * avec le bon libellé métier.
         */
        verify(this.typeProduitDaoJPA, times(1))
            .findByTypeProduitIgnoreCase(VETEMENT);

    } // __________________________________________________________________
    

		
    /**
     * <div>
     * <p>garantit que findByObjetMetier(KO DAO message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway} ;</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE} ;</li>
     * <li>émet un message sûr non nul dérivé de l'exception technique ;</li>
     * <li>propage comme cause l'exception technique d'origine ;</li>
     * <li>appelle le DAO une fois avec le bon libellé métier.</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(KO DAO message null) - jette ExceptionTechniqueGateway avec message sûr non nul")
    @Test
    public void testFindByObjetMetierDAOExceptionMessageNull() {

        /* ARRANGE :
         * prépare un objet métier valide
         * afin d'atteindre réellement la recherche dans le stockage.
         */
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, null);

        /* Configure le DAO mocké avec Mockito
         * pour que findByTypeProduitIgnoreCase(VETEMENT)
         * jette une RuntimeException sans message.
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.typeProduitDaoJPA.findByTypeProduitIgnoreCase(VETEMENT))
            .thenThrow(causeDao);

        /* ACT :
         * exécute une seule fois service.findByObjetMetier(metier)
         * et capture l'exception réellement levée.
         */
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.findByObjetMetier(metier));

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
         * avec la dépendance mockée.
         * Assure que le DAO objet métier a été appelé une fois
         * avec le bon libellé métier.
         */
        verify(this.typeProduitDaoJPA, times(1))
            .findByTypeProduitIgnoreCase(VETEMENT);

    } // __________________________________________________________________
    
	

    /**
	 * <div>
	 * <p>garantit que findByObjetMetier(non trouvé) :</p>
	 * <ul>
	 * <li>délègue la recherche au DAO avec le bon libellé métier ;</li>
	 * <li>retourne {@code null} si le stockage ne trouve aucun
	 * objet persistant correspondant ;</li>
	 * <li>ne jette aucune exception ;</li>
	 * <li>appelle le DAO une fois.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Tag(TAG_FINDBYOBJETMETIER)
	@DisplayName("findByObjetMetier(non trouvé) - retourne null")
	@Test
	public void testFindByObjetMetierNonTrouve() throws Exception {
	
	    /* ARRANGE :
	     * prépare un objet métier valide
	     * dont le libellé doit être recherché dans le stockage.
	     */
	    final TypeProduit metier = fabriquerTypeProduit(VETEMENT, null);
	
	    /*
	     * Configuration du Mock :
	     * L'appel typeProduitDaoJPA.findByTypeProduitIgnoreCase(...)
	     * sur le DAO objet métier mocké retourne null.
	     *
	     * Ce scénario simule un stockage qui ne trouve
	     * aucun TypeProduit persistant correspondant
	     * au libellé métier recherché.
	     */
	    when(this.typeProduitDaoJPA.findByTypeProduitIgnoreCase(VETEMENT))
	        .thenReturn(null);
	
	    /* ACT :
	     * appelle service.findByObjetMetier(metier)
	     * dans le scénario où aucun objet persistant ne correspond.
	     */
	    final TypeProduit resultat = this.service.findByObjetMetier(metier);
	
	    /* ASSERT :
	     * vérifie que la méthode retourne null
	     * lorsque le libellé métier recherché
	     * n'est pas trouvé dans le stockage.
	     */
	    assertThat(resultat).isNull();
	
	    /* Garantit que le DAO mocké n'a pas été appelé. */
	    /* - verify(..., never()).méthode(...) = preuve ciblée 
	     * sur une méthode critique précise.
	     * - verifyNoInteractions(mock) = preuve globale 
	     * que le mock entier n'a pas été touché. */
	    verify(this.typeProduitDaoJPA, times(1))
	        .findByTypeProduitIgnoreCase(VETEMENT);
	
	} // __________________________________________________________________



	/**
     * <div>
     * <p>garantit que findByObjetMetier(OK) :</p>
     * <ul>
     * <li>délègue la recherche au DAO avec le bon libellé métier ;</li>
     * <li>retourne un {@link TypeProduit} non null lorsque le DAO
     * retourne un objet persistant correspondant ;</li>
     * <li>retourne un objet métier converti portant le bon identifiant ;</li>
     * <li>retourne un objet métier converti portant le bon libellé ;</li>
     * <li>appelle le DAO une fois avec le bon libellé métier.</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(OK) - délègue au DAO et retourne l'objet métier converti")
    @Test
    public void testFindByObjetMetierNominal() throws Exception {

        /* ARRANGE :
         * prépare un objet métier valide
         * dont le libellé doit être recherché dans le stockage.
         */
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, null);

        /*
         * Configuration du Mock :
         * L'appel typeProduitDaoJPA.findByTypeProduitIgnoreCase(VETEMENT)
         * sur le DAO objet métier mocké retourne un TypeProduitJPA
         * persistant portant l'identifiant ID_1.
         *
         * Ce scénario simule un stockage qui retourne
         * un TypeProduit persistant correspondant
         * au libellé métier recherché.
         */
        when(this.typeProduitDaoJPA.findByTypeProduitIgnoreCase(VETEMENT))
            .thenReturn(fabriquerTypeProduitJPA(VETEMENT, ID_1));

        /* ACT :
         * appelle service.findByObjetMetier(metier)
         * dans le scénario nominal.
         */
        final TypeProduit resultat = this.service.findByObjetMetier(metier);

        /* ASSERT :
         * vérifie que la méthode retourne un objet métier converti
         * portant l'identifiant et le libellé issus du stockage.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdTypeProduit()).isEqualTo(ID_1);
        assertThat(resultat.getTypeProduit()).isEqualTo(VETEMENT);

        /*
         * Vérifie ensuite les interactions réelles
         * avec la dépendance mockée.
         * Assure que le DAO objet métier a été appelé une fois
         * avec le bon libellé métier.
         */
        verify(this.typeProduitDaoJPA, times(1))
            .findByTypeProduitIgnoreCase(VETEMENT);

    } // __________________________________________________________________
    
    

    // ========================== findByLibelle ===========================

    
    
    /**
     * <div>
     * <p>garantit que findByLibelle(blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank}</li>
     * <li>émet un
     * {@link TypeProduitGatewayIService#MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK}</li>
     * <li>n'appelle pas le DAO</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelle(blank) : jette ExceptionAppliLibelleBlank et n'appelle pas le DAO")
    @Test
    public void testFindByLibelleBlank() {
    	
    	/* ARRANGE - ACT - ASSERT */
    	/* Garantit que this.service.findByLibelle(BLANK)
    	 * - jette une ExceptionAppliLibelleBlank
    	 * - émet un message MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK.
    	 */
        assertThatThrownBy(() -> this.service.findByLibelle(BLANK))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK);
        
        /* Garantit que le DAO mocké n'a pas été appelé. */
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que findByLibelle(non trouvé) :</p>
     * <ul>
     * <li>délègue la recherche au DAO avec le bon libellé métier</li>
     * <li>retourne {@code null} si le stockage ne trouve aucun 
     * objet persistant sans jeter d'Exception</li>
     * <li>appelle le DAO une fois</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelle(non trouvé) : délègue au DAO et retourne null")
    @Test
    public void testFindByLibelleNonTrouve() throws Exception {
    	
    	/* ARRANGE :
    	 * configure ici le comportement du DAO mocké avec Mockito.
    	 *
    	 * La formule when(...).thenReturn(...) signifie :
    	 * "si, pendant le test, le service appelle le DAO mocké avec Mockito
    	 * avec le libellé VETEMENT via findByTypeProduitIgnoreCase(...),
    	 * alors le DAO mocké avec Mockito devra répondre null".
    	 *
    	 * On simule donc volontairement un stockage
    	 * qui ne trouve aucun TypeProduit persistant
    	 * pour ce libellé métier.
    	 *
    	 * Le but n'est pas de tester le DAO réel,
    	 * mais de maîtriser sa réponse
    	 * afin de prouver comment le service réagit
    	 * au cas contractuel "non trouvé".
    	 */
        when(this.typeProduitDaoJPA.findByTypeProduitIgnoreCase(VETEMENT))
            .thenReturn(null);
    	
    	/* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester 
         * dans les conditions voulues par le Mock (when du ARRANGE). */
        final TypeProduit resultat = this.service.findByLibelle(VETEMENT);
    	
    	/* ASSERT */
    	/* Garantit que this.service.findByLibelle(VETEMENT)
    	 * - délègue la recherche au DAO avec le libellé métier VETEMENT
    	 * - retourne null si le stockage ne trouve aucun objet persistant.
    	 */
        assertThat(resultat).isNull();
        
        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon libellé métier.
         */
        verify(this.typeProduitDaoJPA).findByTypeProduitIgnoreCase(VETEMENT);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que findByLibelle(KO DAO) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelle(KO DAO) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testFindByLibelleExceptionDAO() {
    	
    	/* ARRANGE :
    	 * configure ici le comportement du DAO mocké avec Mockito.
    	 *
    	 * La formule when(...).thenThrow(...) signifie :
    	 * "si, pendant le test, le service appelle le DAO mocké avec Mockito
    	 * avec le libellé VETEMENT via findByTypeProduitIgnoreCase(...),
    	 * alors le DAO mocké avec Mockito devra lancer
    	 * une RuntimeException portant le message MSG_BOOM".
    	 *
    	 * On simule donc volontairement une panne technique du stockage
    	 * pendant la recherche par libellé métier.
    	 *
    	 * Le but n'est pas de tester le DAO réel,
    	 * mais de maîtriser sa réaction
    	 * afin de prouver comment le service réagit
    	 * au cas contractuel "KO DAO".
    	 */
    	final RuntimeException causeDao = new RuntimeException(MSG_BOOM);
    	
        when(this.typeProduitDaoJPA.findByTypeProduitIgnoreCase(VETEMENT))
            .thenThrow(causeDao);
    	
    	/* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester 
         * dans les conditions voulues par le Mock (when du ARRANGE). */
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.findByLibelle(VETEMENT));
    	
    	/* ASSERT */
    	/* Garantit que this.service.findByLibelle(VETEMENT)
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
         * avec le bon libellé métier.
         */
        verify(this.typeProduitDaoJPA).findByTypeProduitIgnoreCase(VETEMENT);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findByLibelle(KO DAO message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non nul dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelle(KO DAO message null) : jette ExceptionTechniqueGateway avec message sûr non nul")
    @Test
    public void testFindByLibelleExceptionDAOMsgNull() {
    	
    	/* ARRANGE :
    	 * configure ici le comportement du DAO mocké avec Mockito.
    	 *
    	 * La formule when(...).thenThrow(...) signifie :
    	 * "si, pendant le test, le service appelle le DAO mocké avec Mockito
    	 * avec le libellé VETEMENT via findByTypeProduitIgnoreCase(...),
    	 * alors le DAO mocké avec Mockito devra lancer
    	 * une RuntimeException sans message".
    	 *
    	 * On simule donc volontairement une panne technique du stockage
    	 * pendant la recherche par libellé métier,
    	 * avec un message technique d'origine null.
    	 *
    	 * Le but n'est pas de tester le DAO réel,
    	 * mais de maîtriser sa réaction
    	 * afin de prouver comment le service réagit
    	 * au cas contractuel "KO DAO message null".
    	 */
    	final RuntimeException causeDao = new RuntimeException((String) null);
    	
        when(this.typeProduitDaoJPA.findByTypeProduitIgnoreCase(VETEMENT))
            .thenThrow(causeDao);
    	
    	/* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester 
         * dans les conditions voulues par le Mock (when du ARRANGE). */
        /* Exécute une seule fois this.service.findByLibelle(VETEMENT)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.*/
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.findByLibelle(VETEMENT));
    	
    	/* ASSERT */
    	/* Garantit que this.service.findByLibelle(VETEMENT)
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
        
        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon libellé métier.
         */
        verify(this.typeProduitDaoJPA).findByTypeProduitIgnoreCase(VETEMENT);
        
    } // __________________________________________________________________
    
    

    // ======================== findByLibelleRapide =======================
    
    

    /**
     * <div>
     * <p>garantit que findByLibelleRapide(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull}</li>
     * <li>émet un
     * {@link TypeProduitGatewayIService#MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL}</li>
     * <li>n'appelle pas le DAO</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName("findByLibelleRapide(null) : jette ExceptionAppliParamNull et n'appelle pas le DAO")
    @Test
    public void testFindByLibelleRapideNull() {
    	
    	/* ARRANGE - ACT - ASSERT */
    	/* Garantit que this.service.findByLibelleRapide(null)
    	 * - jette une ExceptionAppliParamNull
    	 * - émet un message MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL.
    	 */
        assertThatThrownBy(() -> this.service.findByLibelleRapide(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL);
        
        /* Garantit que le DAO mocké n'a pas été appelé. */
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que findByLibelleRapide(blank) :</p>
     * <ul>
     * <li>délègue au DAO la recherche complète via {@code findAll()}</li>
     * <li>retourne une {@link List} non null</li>
     * <li>retourne tous les objets métier issus du stockage</li>
     * <li>n'appelle jamais la recherche rapide DAO dédiée</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName("findByLibelleRapide(blank) : délègue à findAll()")
    @Test
    public void testFindByLibelleRapideBlankDelegueFindAll() throws Exception {
    	
    	/* ARRANGE :
    	 * configure ici le comportement du DAO mocké avec Mockito.
    	 *
    	 * La formule when(...).thenReturn(...) signifie :
    	 * "si, pendant le test, le service appelle le DAO mocké avec Mockito
    	 * via findAll(),
    	 * alors le DAO mocké avec Mockito devra répondre
    	 * une liste complète de TypeProduitJPA persistants".
    	 *
    	 * On simule donc volontairement un stockage
    	 * contenant plusieurs objets persistants,
    	 * afin de prouver que, lorsque le libellé rapide est blank,
    	 * le service ne lance pas une recherche filtrée
    	 * mais délègue bien au DAO une recherche complète.
    	 *
    	 * Le but n'est pas de tester le DAO réel,
    	 * mais de maîtriser sa réponse
    	 * afin de prouver comment le service réagit
    	 * au cas contractuel "blank délègue findAll()".
    	 */
        when(this.typeProduitDaoJPA.findAll()).thenReturn(Arrays.asList(
                fabriquerTypeProduitJPA(OUTILLAGE, ID_2),
                fabriquerTypeProduitJPA(VETEMENT, ID_1)));
    	
    	/* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester 
         * dans les conditions voulues par le Mock (when du ARRANGE). */
        final List<TypeProduit> resultat = this.service.findByLibelleRapide(BLANK);
    	
    	/* ASSERT */
    	/* Garantit que this.service.findByLibelleRapide(BLANK)
    	 * - retourne une liste non null
    	 * - retourne tous les objets métier issus du stockage
    	 *   lorsque le libellé rapide est blank.
    	 */
        assertThat(resultat).isNotNull().hasSize(2);
        
        /* Garantit que le DAO mocké a bien été appelé une fois
         * via findAll().
         */
        verify(this.typeProduitDaoJPA).findAll();
        
        /* Garantit que le DAO mocké n'a jamais été appelé
         * via la recherche rapide dédiée
         * findByTypeProduitContainingIgnoreCase(...).
         */
        verify(this.typeProduitDaoJPA, never())
            .findByTypeProduitContainingIgnoreCase(any(String.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que findByLibelleRapide(DAO retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet le message
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE}</li>
     * <li>appelle le DAO une fois avec le bon contenu de recherche rapide</li>
     * <li>n'appelle jamais {@code findAll()}</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName("findByLibelleRapide(DAO retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testFindByLibelleRapideDAORetourneNull() {

        /* ARRANGE :
         * configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec le contenu RECHERCHE_ME via
         * findByTypeProduitContainingIgnoreCase(...),
         * alors le DAO mocké avec Mockito devra répondre null".
         *
         * On simule donc volontairement un stockage
         * qui retourne null au lieu d'une collection persistante.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réponse
         * afin de prouver comment le service réagit
         * au cas contractuel "DAO retourne null".
         */
        when(this.typeProduitDaoJPA.findByTypeProduitContainingIgnoreCase(RECHERCHE_ME))
            .thenReturn(null);

        /* ACT - ASSERT */
        /* Garantit que this.service.findByLibelleRapide(RECHERCHE_ME)
         * - jette une ExceptionTechniqueGateway
         * - émet exactement le message ERREUR_TECHNIQUE_KO_STOCKAGE.
         */
        assertThatThrownBy(() -> this.service.findByLibelleRapide(RECHERCHE_ME))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon contenu de recherche rapide.
         */
        verify(this.typeProduitDaoJPA)
            .findByTypeProduitContainingIgnoreCase(RECHERCHE_ME);

        /* Garantit que le DAO mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.typeProduitDaoJPA, never()).findAll();

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelleRapide(aucun résultat) :</p>
     * <ul>
     * <li>délègue au DAO la recherche rapide via
     * {@code findByTypeProduitContainingIgnoreCase(...)}</li>
     * <li>retourne une {@link List} non null</li>
     * <li>retourne une liste vide</li>
     * <li>n'appelle jamais {@code findAll()}</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName("findByLibelleRapide(aucun résultat) : retourne une liste vide non null")
    @Test
    public void testFindByLibelleRapideAucunResultat() throws Exception {

        /* ARRANGE :
         * configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec le contenu RECHERCHE_ME via
         * findByTypeProduitContainingIgnoreCase(...),
         * alors le DAO mocké avec Mockito devra répondre
         * une liste vide".
         *
         * On simule donc volontairement un stockage
         * qui ne trouve aucun enregistrement correspondant.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réponse
         * afin de prouver comment le service réagit
         * au cas contractuel "aucun résultat".
         */
        when(this.typeProduitDaoJPA.findByTypeProduitContainingIgnoreCase(RECHERCHE_ME))
            .thenReturn(Collections.emptyList());

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        final List<TypeProduit> resultat =
            this.service.findByLibelleRapide(RECHERCHE_ME);

        /* ASSERT */
        /* Garantit que this.service.findByLibelleRapide(RECHERCHE_ME)
         * - retourne une liste non null
         * - retourne une liste vide
         *   lorsque le stockage ne trouve aucun résultat.
         */
        assertThat(resultat).isNotNull().isEmpty();

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon contenu de recherche rapide.
         */
        verify(this.typeProduitDaoJPA)
            .findByTypeProduitContainingIgnoreCase(RECHERCHE_ME);

        /* Garantit que le DAO mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.typeProduitDaoJPA, never()).findAll();

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelleRapide(nominal) :</p>
     * <ul>
     * <li>délègue au DAO la recherche rapide via
     * {@code findByTypeProduitContainingIgnoreCase(...)}</li>
     * <li>retourne une {@link List} non null</li>
     * <li>filtre les éléments null issus du stockage</li>
     * <li>dédoublonne les libellés au sens métier
     * sans tenir compte de la casse</li>
     * <li>retourne les objets métier triés par libellé</li>
     * <li>n'appelle jamais {@code findAll()}</li>
     * </ul>
     * <p>Ce test prouve directement au niveau de la méthode publique
     * le filtrage des null,
     * le dédoublonnage insensible à la casse
     * et le tri final par libellé.</p>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName("findByLibelleRapide(nominal) : filtre les null, trie et dédoublonne sans tenir compte de la casse")
    @Test
    public void testFindByLibelleRapideNominalFiltreTrieDedoublonne() throws Exception {

        /* ARRANGE :
         * configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec le contenu RECHERCHE_ME via
         * findByTypeProduitContainingIgnoreCase(...),
         * alors le DAO mocké avec Mockito devra répondre
         * une collection persistante non triée,
         * contenant un null
         * et un doublon métier à la casse près".
         *
         * On simule donc volontairement un stockage
         * qui retourne :
         * - un élément null,
         * - deux libellés fonctionnellement identiques
         *   ne différant que par la casse,
         * - plusieurs valeurs non triées.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réponse
         * afin de prouver comment le service réagit
         * au cas contractuel "scénario nominal complet".
         */
        final String libelle1 = "meuble";
        final String libelle2 = "MECANIQUE";
        final String libelle3 = "gamme";
        final String libelle4 = libelle1.toUpperCase(Locale.ROOT);

        final List<TypeProduitJPA> entities = Arrays.asList(
                fabriquerTypeProduitJPA(libelle1, ID_1),
                null,
                fabriquerTypeProduitJPA(libelle2, ID_2),
                fabriquerTypeProduitJPA(libelle3, ID_3),
                fabriquerTypeProduitJPA(libelle4, ID_4));

        when(this.typeProduitDaoJPA.findByTypeProduitContainingIgnoreCase(RECHERCHE_ME))
            .thenReturn(entities);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        final List<TypeProduit> resultat =
            this.service.findByLibelleRapide(RECHERCHE_ME);

        /* ASSERT */
        /* Garantit que this.service.findByLibelleRapide(RECHERCHE_ME)
         * - retourne une liste non null
         * - ne contient plus aucun élément null
         * - dédoublonne les libellés au sens métier
         *   sans tenir compte de la casse
         * - restitue le résultat final trié par libellé.
         *
         * L'extraction normalisée en trim + lowerCase(Locale.ROOT)
         * est volontaire :
         * elle permet de prouver directement le dédoublonnage métier
         * et le tri final
         * sans sur-contraindre artificiellement
         * la casse conservée par l'implémentation.
         */
        assertThat(resultat).isNotNull().doesNotContainNull().hasSize(3);
        assertThat(resultat)
            .extracting(typeProduit -> typeProduit.getTypeProduit()
                .trim()
                .toLowerCase(Locale.ROOT))
            .containsExactly(
                libelle3,
                libelle2.toLowerCase(Locale.ROOT),
                libelle1);

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon contenu de recherche rapide.
         */
        verify(this.typeProduitDaoJPA)
            .findByTypeProduitContainingIgnoreCase(RECHERCHE_ME);

        /* Garantit que le DAO mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.typeProduitDaoJPA, never()).findAll();

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelleRapide(KO DAO message non nul) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois</li>
     * <li>n'appelle jamais {@code findAll()}</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName("findByLibelleRapide(KO DAO message non nul) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testFindByLibelleRapideExceptionDAO() {

        /* ARRANGE :
         * configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec le contenu RECHERCHE_ME via
         * findByTypeProduitContainingIgnoreCase(...),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException portant le message MSG_BOOM".
         *
         * On simule donc volontairement une panne technique du stockage
         * pendant la recherche rapide par contenu.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réaction
         * afin de prouver comment le service réagit
         * au cas contractuel "KO DAO message non nul".
         */
        final RuntimeException causeDao = new RuntimeException(MSG_BOOM);

        when(this.typeProduitDaoJPA.findByTypeProduitContainingIgnoreCase(RECHERCHE_ME))
            .thenThrow(causeDao);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE). */
        /* Exécute une seule fois this.service.findByLibelleRapide(RECHERCHE_ME)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.findByLibelleRapide(RECHERCHE_ME));

        /* ASSERT */
        /* Garantit que this.service.findByLibelleRapide(RECHERCHE_ME)
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
         * avec le bon contenu de recherche rapide.
         */
        verify(this.typeProduitDaoJPA)
            .findByTypeProduitContainingIgnoreCase(RECHERCHE_ME);

        /* Garantit que le DAO mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.typeProduitDaoJPA, never()).findAll();

    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que findByLibelleRapide(KO DAO message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non nul dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois</li>
     * <li>n'appelle jamais {@code findAll()}</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName("findByLibelleRapide(KO DAO message null) : jette ExceptionTechniqueGateway avec message sûr non nul")
    @Test
    public void testFindByLibelleRapideExceptionDAOMsgNull() {

        /* ARRANGE :
         * configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec le contenu RECHERCHE_ME via
         * findByTypeProduitContainingIgnoreCase(...),
         * alors le DAO mocké avec Mockito devra lancer
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

        when(this.typeProduitDaoJPA.findByTypeProduitContainingIgnoreCase(RECHERCHE_ME))
            .thenThrow(causeDao);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE). */
        /* Exécute une seule fois this.service.findByLibelleRapide(RECHERCHE_ME)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.findByLibelleRapide(RECHERCHE_ME));

        /* ASSERT */
        /* Garantit que this.service.findByLibelleRapide(RECHERCHE_ME)
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

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon contenu de recherche rapide.
         */
        verify(this.typeProduitDaoJPA)
            .findByTypeProduitContainingIgnoreCase(RECHERCHE_ME);

        /* Garantit que le DAO mocké n'a jamais été appelé
         * via findAll().
         */
        verify(this.typeProduitDaoJPA, never()).findAll();

    } // __________________________________________________________________
    

    
    // ============================ findById ==============================

    
    
    /**
     * <div>
     * <p>garantit que findById(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull}</li>
     * <li>émet un
     * {@link TypeProduitGatewayIService#MESSAGE_FINDBYID_KO_PARAM_NULL}</li>
     * <li>n'appelle pas le DAO</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findById(null) : jette ExceptionAppliParamNull et n'appelle pas le DAO")
    @Test
    public void testFindByIdNull() {
    	
        /* ARRANGE - ACT - ASSERT */
        /* Garantit que this.service.findById(null)
         * - jette une ExceptionAppliParamNull
         * - émet un message MESSAGE_FINDBYID_KO_PARAM_NULL.
         */
        assertThatThrownBy(() -> this.service.findById(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYID_KO_PARAM_NULL);

        /* Garantit que le DAO mocké n'a pas été appelé. */
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que findById(DAO retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet le message
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE}</li>
     * <li>appelle le DAO une fois avec le bon identifiant</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findById(DAO retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testFindByIdDAORetourneNull() {
    	
        /* ARRANGE :
         * configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_1 via findById(...),
         * alors le DAO mocké avec Mockito devra répondre null".
         *
         * On simule donc volontairement un stockage
         * qui retourne null au lieu d'un Optional<TypeProduitJPA>.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réponse
         * afin de prouver comment le service réagit
         * au cas contractuel "DAO retourne null".
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(null);
        
        /* ACT - ASSERT */
        /* Garantit que this.service.findById(ID_1)
         * - jette une ExceptionTechniqueGateway
         * - émet exactement le message ERREUR_TECHNIQUE_KO_STOCKAGE.
         */
        assertThatThrownBy(() -> this.service.findById(ID_1))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon identifiant.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que findById(ID inexistant) :</p>
     * <ul>
     * <li>délègue la recherche au DAO avec le bon identifiant inexistant</li>
     * <li>retourne {@code null} si le stockage ne trouve aucun
     * objet persistant pour cet identifiant sans jeter d'Exception</li>
     * <li>appelle le DAO une fois</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findById(ID inexistant) : délègue au DAO et retourne null")
    @Test
    public void testFindByIdInexistant() throws Exception {
    	
        /* ARRANGE :
         * configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant inexistant ID_INEXISTANT via findById(...),
         * alors le DAO mocké avec Mockito devra répondre
         * Optional.empty()".
         *
         * On simule donc volontairement un stockage
         * qui ne trouve aucun TypeProduit persistant
         * pour cet identifiant inexistant.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réponse
         * afin de prouver comment le service réagit
         * au cas contractuel "ID inexistant".
         */
        when(this.typeProduitDaoJPA.findById(ID_INEXISTANT))
            .thenReturn(Optional.empty());
        
        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        final TypeProduit resultat = this.service.findById(ID_INEXISTANT);
        
        /* ASSERT */
        /* Garantit que this.service.findById(ID_INEXISTANT)
         * retourne null
         * lorsque le stockage ne trouve aucun objet persistant
         * pour l'identifiant inexistant demandé.
         */
        assertThat(resultat).isNull();

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon identifiant inexistant.
         */
        verify(this.typeProduitDaoJPA).findById(ID_INEXISTANT);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que findById(trouvé) :</p>
     * <ul>
     * <li>délègue la recherche au DAO avec le bon identifiant</li>
     * <li>retourne un objet métier non null</li>
     * <li>convertit correctement l'identifiant et le libellé
     * de l'objet persistant trouvé</li>
     * <li>appelle le DAO une fois</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findById(trouvé) : délègue au DAO et retourne l'objet métier converti")
    @Test
    public void testFindByIdTrouve() throws Exception {
    	
        /* ARRANGE :
         * configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_1 via findById(...),
         * alors le DAO mocké avec Mockito devra répondre
         * Optional.of(...) contenant un TypeProduitJPA persistant
         * portant l'identifiant ID_1 et le libellé VETEMENT".
         *
         * On simule donc volontairement un stockage
         * qui trouve bien un TypeProduit persistant
         * pour cet identifiant.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réponse
         * afin de prouver comment le service réagit
         * au cas contractuel "trouvé".
         */
        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.of(fabriquerTypeProduitJPA(VETEMENT, ID_1)));
        
        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        final TypeProduit resultat = this.service.findById(ID_1);
        
        /* ASSERT */
        /* Garantit que this.service.findById(ID_1)
         * - délègue la recherche au DAO avec l'identifiant ID_1
         * - retourne un objet métier non null
         * - convertit correctement l'identifiant et le libellé
         *   de l'objet persistant trouvé.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdTypeProduit()).isEqualTo(ID_1);
        assertThat(resultat.getTypeProduit()).isEqualTo(VETEMENT);

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon identifiant.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que findById(KO DAO message non nul) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois avec le bon identifiant</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findById(KO DAO message non nul) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testFindByIdExceptionDAO() {
    	
        /* ARRANGE :
         * configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_1 via findById(...),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException portant le message MSG_BOOM".
         *
         * On simule donc volontairement une panne technique du stockage
         * pendant la recherche par identifiant.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réaction
         * afin de prouver comment le service réagit
         * au cas contractuel "KO DAO message non nul".
         */
        final RuntimeException causeDao = new RuntimeException(MSG_BOOM);
    	
        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenThrow(causeDao);
        
        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE). */
        /* Exécute une seule fois this.service.findById(ID_1)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.findById(ID_1));
        
        /* ASSERT */
        /* Garantit que this.service.findById(ID_1)
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
         * avec le bon identifiant.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que findById(KO DAO message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non nul dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois avec le bon identifiant</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findById(KO DAO message null) : jette ExceptionTechniqueGateway avec message sûr non nul")
    @Test
    public void testFindByIdExceptionDAOMsgNull() {
    	
        /* ARRANGE :
         * configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_1 via findById(...),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException sans message".
         *
         * On simule donc volontairement une panne technique du stockage
         * pendant la recherche par identifiant,
         * avec un message technique d'origine null.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réaction
         * afin de prouver comment le service réagit
         * au cas contractuel "KO DAO message null".
         */
        final RuntimeException causeDao = new RuntimeException((String) null);
    	
        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenThrow(causeDao);
        
        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE). */
        /* Exécute une seule fois this.service.findById(ID_1)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.findById(ID_1));
        
        /* ASSERT */
        /* Garantit que this.service.findById(ID_1)
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

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon identifiant.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        
    } // __________________________________________________________________
    
    

    // ============================= update ===============================
    
    

    /**
     * <div>
     * <p>garantit que update(paramètre null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull}</li>
     * <li>émet un message
     * {@link TypeProduitGatewayIService#MESSAGE_UPDATE_KO_PARAM_NULL}</li>
     * <li>n'appelle pas le DAO</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(null) : jette ExceptionAppliParamNull et n'appelle pas le DAO")
    @Test
    public void testUpdateNull() {
    	
        /* ARRANGE - ACT - ASSERT */
        /* Garantit que this.service.update(null)
         * - jette une ExceptionAppliParamNull
         * - émet un message MESSAGE_UPDATE_KO_PARAM_NULL.
         */
        assertThatThrownBy(() -> this.service.update(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_UPDATE_KO_PARAM_NULL);

        /* Garantit que le DAO mocké n'a pas été appelé. */
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(libellé null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank}</li>
     * <li>émet un message
     * {@link TypeProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_BLANK}</li>
     * <li>n'appelle pas le DAO</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(libellé null) : jette ExceptionAppliLibelleBlank et n'appelle pas le DAO")
    @Test
    public void testUpdateLibelleNull() {
    	
        /* ARRANGE */
        final TypeProduit metier = fabriquerTypeProduit(null, ID_1);
        
        /* ACT - ASSERT */
        /* Garantit que this.service.update(metier)
         * - jette une ExceptionAppliLibelleBlank
         * - émet un message MESSAGE_UPDATE_KO_LIBELLE_BLANK.
         */
        assertThatThrownBy(() -> this.service.update(metier))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_BLANK);

        /* Garantit que le DAO mocké n'a pas été appelé. */
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que update(libellé blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank}</li>
     * <li>émet un message
     * {@link TypeProduitGatewayIService#MESSAGE_UPDATE_KO_LIBELLE_BLANK}</li>
     * <li>n'appelle pas le DAO</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(libellé blank) : jette ExceptionAppliLibelleBlank et n'appelle pas le DAO")
    @Test
    public void testUpdateBlank() {
    	
        /* ARRANGE */
        final TypeProduit metier = fabriquerTypeProduit(BLANK, ID_1);
        
        /* ACT - ASSERT */
        /* Garantit que this.service.update(metier)
         * - jette une ExceptionAppliLibelleBlank
         * - émet un message MESSAGE_UPDATE_KO_LIBELLE_BLANK.
         */
        assertThatThrownBy(() -> this.service.update(metier))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_BLANK);

        /* Garantit que le DAO mocké n'a pas été appelé. */
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>garantit que update(libellé déjà porté par un autre TypeProduit) :</p>
     * <ul>
     * <li>ne lève aucune exception</li>
     * <li>ne déclenche aucune sauvegarde</li>
     * <li>retourne l'objet persistant inchangé</li>
     * <li>contrôle le conflit fonctionnel via le DAO</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(libellé déjà existant sur un autre TypeProduit) : retourne l'objet persistant inchangé sans save()")
    @Test
    public void testUpdateLibelleExistant() throws Exception {
    	
        /* ARRANGE :
         * configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_1 via findById(...),
         * alors le DAO mocké avec Mockito devra répondre
         * l'objet persistant actuellement stocké".
         *
         * On simule donc volontairement un objet persistant courant
         * portant encore le libellé VETEMENT.
         */
        final TypeProduitJPA persistee = fabriquerTypeProduitJPA(VETEMENT, ID_1);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.of(persistee));

        /* Configure ensuite le contrôle de conflit fonctionnel :
         * le DAO mocké avec Mockito trouve déjà un autre TypeProduit
         * portant le libellé CAMPING,
         * mais avec un identifiant différent ID_2.
         *
         * Cela simule exactement le cas contractuel :
         * le nouveau libellé demandé correspond déjà
         * à celui d'un autre TypeProduit présent dans le stockage.
         */
        final TypeProduitJPA autrePersistant = fabriquerTypeProduitJPA(CAMPING, ID_2);

        when(this.typeProduitDaoJPA.findByTypeProduitIgnoreCase(CAMPING))
            .thenReturn(autrePersistant);

        final TypeProduit metier = fabriquerTypeProduit(CAMPING, ID_1);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        final TypeProduit resultat = this.service.update(metier);

        /* ASSERT */
        /* Garantit que this.service.update(metier)
         * - ne lève aucune exception
         * - ne modifie pas l'objet persistant courant
         *   lorsqu'un autre TypeProduit possède déjà le libellé demandé
         * - retourne donc l'objet persistant inchangé.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdTypeProduit()).isEqualTo(ID_1);
        assertThat(resultat.getTypeProduit()).isEqualTo(VETEMENT);

        /* Garantit que le DAO mocké a bien été interrogé
         * sur l'objet persistant courant à mettre à jour.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);

        /* Garantit que le DAO mocké a bien contrôlé
         * l'existence d'un autre TypeProduit portant déjà le libellé demandé.
         */
        verify(this.typeProduitDaoJPA).findByTypeProduitIgnoreCase(CAMPING);

        /* Garantit qu'aucune sauvegarde n'a été déclenchée,
         * puisque le contrat impose
         * qu'aucune modification ne soit appliquée dans ce cas.
         */
        verify(this.typeProduitDaoJPA, never()).save(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que update(pObject avec ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNonPersistent}</li>
     * <li>émet un message
     * {@link TypeProduitGatewayIService#MESSAGE_UPDATE_KO_NON_PERSISTENT}
     * suivi du libellé de l'objet métier</li>
     * <li>n'appelle pas le DAO</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(ID null) : jette ExceptionAppliParamNonPersistent et n'appelle pas le DAO")
    @Test
    public void testUpdateIdNull() {
    	
        /* ARRANGE */
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, null);

        /* ACT - ASSERT */
        /* Garantit que this.service.update(metier)
         * - jette une ExceptionAppliParamNonPersistent
         * - émet un message MESSAGE_UPDATE_KO_NON_PERSISTENT
         *   suivi du libellé VETEMENT.
         */
        assertThatThrownBy(() -> this.service.update(metier))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_UPDATE_KO_NON_PERSISTENT + VETEMENT);

        /* Garantit que le DAO mocké n'a pas été appelé. */
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que update(DAO.findById retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE}</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>ne déclenche aucune sauvegarde</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(findById retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE et ne sauvegarde pas")
    @Test
    public void testUpdateFindByIdDAORetourneNull() {
    	
        /* ARRANGE */
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_1 via findById(...),
         * alors le DAO mocké avec Mockito devra répondre null".
         *
         * On simule donc volontairement un stockage
         * qui retourne null au lieu d'un Optional<TypeProduitJPA>,
         * ce qui doit être interprété comme une anomalie technique
         * de stockage.
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(null);

        /* ACT - ASSERT */
        /* Garantit que this.service.update(metier)
         * - jette une ExceptionTechniqueGateway
         * - émet un message ERREUR_TECHNIQUE_KO_STOCKAGE
         *   lorsque le stockage retourne null sur findById(...).
         */
        assertThatThrownBy(() -> this.service.update(metier))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon identifiant via findById(...).
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);

        /* Garantit qu'aucune sauvegarde n'a été déclenchée,
         * puisque le chargement de l'objet persistant a échoué
         * avant toute modification.
         */
        verify(this.typeProduitDaoJPA, never()).save(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que update(entity inexistante dans le stockage) :</p>
     * <ul>
     * <li>ne lève aucune exception</li>
     * <li>retourne {@code null}</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>ne déclenche aucune sauvegarde</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(entity inexistante dans le stockage) : retourne null et ne sauvegarde pas")
    @Test
    public void testUpdateEntityInexistante() throws Exception {
    	
        /* ARRANGE */
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_1 via findById(...),
         * alors le DAO mocké avec Mockito devra répondre
         * Optional.empty()".
         *
         * On simule donc volontairement un stockage
         * qui ne trouve aucun TypeProduit persistant
         * pour l'identifiant demandé.
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.empty());

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        final TypeProduit resultat = this.service.update(metier);

        /* ASSERT */
        /* Garantit que this.service.update(metier)
         * - retourne null
         *   lorsque l'objet à mettre à jour
         *   n'existe pas dans le stockage
         * - ne lève aucune exception.
         */
        assertThat(resultat).isNull();

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon identifiant via findById(...).
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);

        /* Garantit qu'aucune sauvegarde n'a été déclenchée,
         * puisqu'aucun objet persistant n'a été trouvé
         * à modifier dans le stockage.
         */
        verify(this.typeProduitDaoJPA, never()).save(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que update(aucune modification) :</p>
     * <ul>
     * <li>ne lève aucune exception</li>
     * <li>retourne l'objet persistant inchangé</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>ne déclenche aucune sauvegarde</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(aucune modification) : retourne l'objet persistant inchangé sans save()")
    @Test
    public void testUpdateAucuneModification() throws Exception {
    	
        /* ARRANGE :
         * configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_1 via findById(...),
         * alors le DAO mocké avec Mockito devra répondre
         * l'objet persistant actuellement stocké".
         *
         * On simule donc volontairement un objet persistant courant
         * portant le libellé VETEMENT.
         */
        final TypeProduitJPA persistee = fabriquerTypeProduitJPA(VETEMENT, ID_1);
        
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(persistee));
        
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        final TypeProduit resultat = this.service.update(metier);
        
        /* ASSERT */
        /* Garantit que this.service.update(metier)
         * - ne lève aucune exception
         * - ne modifie pas l'objet persistant courant
         *   lorsque le libellé demandé est déjà celui stocké
         * - retourne donc l'objet persistant inchangé.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdTypeProduit()).isEqualTo(ID_1);
        assertThat(resultat.getTypeProduit()).isEqualTo(VETEMENT);

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon identifiant via findById(...).
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);

        /* Garantit qu'aucune sauvegarde n'a été déclenchée,
         * puisqu'aucune modification n'était à appliquer
         * dans le stockage.
         */
        verify(this.typeProduitDaoJPA, never()).save(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que update(modification effective) :</p>
     * <ul>
     * <li>charge l'objet persistant courant via {@code findById(...)}</li>
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
    @DisplayName("update(modification effective) : sauvegarde dans le stockage et retourne l'objet persistant modifié")
    @Test
    public void testUpdateAvecModification() throws Exception {
    	
        /* ARRANGE :
         * configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie d'abord :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_1 via findById(...),
         * alors le DAO mocké avec Mockito devra répondre
         * l'objet persistant actuellement stocké".
         *
         * On simule donc volontairement un objet persistant courant
         * portant encore le libellé VETEMENT.
         */
        final TypeProduitJPA persistee = fabriquerTypeProduitJPA(VETEMENT, ID_1);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.of(persistee));

        /* La formule when(...).thenReturn(...) signifie ensuite :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * via save(...),
         * alors le DAO mocké avec Mockito devra répondre
         * l'objet persistant modifié".
         *
         * On simule donc volontairement une modification effective
         * dans le stockage :
         * l'identifiant reste ID_1
         * et le nouveau libellé devient CAMPING.
         */
        final TypeProduitJPA modifiee = fabriquerTypeProduitJPA(CAMPING, ID_1);

        when(this.typeProduitDaoJPA.save(any(TypeProduitJPA.class)))
            .thenReturn(modifiee);

        final TypeProduit metier = fabriquerTypeProduit(CAMPING, ID_1);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        final TypeProduit resultat = this.service.update(metier);

        /* ASSERT */
        /* Garantit que this.service.update(metier)
         * - effectue bien une modification dans le stockage
         * - retourne l'objet persistant modifié
         * - conserve l'identifiant ID_1
         * - applique le nouveau libellé CAMPING.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdTypeProduit()).isEqualTo(ID_1);
        assertThat(resultat.getTypeProduit()).isEqualTo(CAMPING);

        /* Garantit que le DAO mocké a bien été interrogé
         * sur l'objet persistant courant à modifier.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);

        /* Garantit qu'une sauvegarde a bien été déclenchée,
         * ce qui prouve qu'une modification effective
         * a été appliquée dans le stockage.
         */
        verify(this.typeProduitDaoJPA).save(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que update(KO DAO sur findById message non nul) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>ne déclenche aucune sauvegarde</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(KO DAO sur findById message non nul) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testUpdateExceptionDAOFindById() {
    	
        /* ARRANGE */
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_1 via findById(...),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException portant le message MSG_BOOM".
         *
         * On simule donc volontairement une panne technique du stockage
         * pendant le chargement de l'objet persistant à mettre à jour.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réaction
         * afin de prouver comment le service réagit
         * au cas contractuel "KO DAO sur findById message non nul".
         */
        final RuntimeException causeDao = new RuntimeException(MSG_BOOM);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenThrow(causeDao);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE). */
        /* Exécute une seule fois this.service.update(metier)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.update(metier));

        /* ASSERT */
        /* Garantit que this.service.update(metier)
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
        verify(this.typeProduitDaoJPA).findById(ID_1);

        /* Garantit qu'aucune sauvegarde n'a été déclenchée,
         * puisque l'échec technique est survenu
         * avant toute modification et avant tout save(...).
         */
        verify(this.typeProduitDaoJPA, never()).save(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>garantit que update(KO DAO sur findById message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non nul dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>ne déclenche aucune sauvegarde</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(KO DAO sur findById message null) : jette ExceptionTechniqueGateway avec message sûr non nul")
    @Test
    public void testUpdateExceptionDAOFindByIdMsgNull() {
    	
        /* ARRANGE */
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_1 via findById(...),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException sans message".
         *
         * On simule donc volontairement une panne technique du stockage
         * pendant le chargement de l'objet persistant à mettre à jour,
         * avec un message technique d'origine null.
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenThrow(causeDao);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE). */
        /* Exécute une seule fois this.service.update(metier)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.update(metier));

        /* ASSERT */
        /* Garantit que this.service.update(metier)
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

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon identifiant via findById(...).
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);

        /* Garantit qu'aucune sauvegarde n'a été déclenchée,
         * puisque l'échec technique est survenu
         * avant toute modification et avant tout save(...).
         */
        verify(this.typeProduitDaoJPA, never()).save(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que update(modification de casse) :</p>
     * <ul>
     * <li>effectue une modification dans le stockage</li>
     * <li>retourne l'objet persistant modifié</li>
     * <li>préserve exactement la casse du nouveau libellé</li>
     * <li>appelle le DAO via {@code findById(...)} puis {@code save(...)}</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(modification de casse) : sauvegarde et préserve exactement la casse du nouveau libellé")
    @Test
    public void testUpdateModificationCasse() throws Exception {
    	
        /* ARRANGE :
         * configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie d'abord :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_1 via findById(...),
         * alors le DAO mocké avec Mockito devra répondre
         * l'objet persistant actuellement stocké".
         *
         * On simule donc volontairement un objet persistant courant
         * portant encore le libellé VETEMENT.
         */
        final TypeProduitJPA persistee = fabriquerTypeProduitJPA(VETEMENT, ID_1);
        
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(persistee));
        
        /* Prépare ensuite une modification limitée à la casse du libellé.
         *
         * L'usage de Locale.ROOT évite toute dépendance
         * au locale de la machine qui exécute le test.
         */
        final String nouveauLibelle = VETEMENT.toUpperCase(Locale.ROOT);
        final TypeProduitJPA modifiee = fabriquerTypeProduitJPA(nouveauLibelle, ID_1);
        
        when(this.typeProduitDaoJPA.save(any(TypeProduitJPA.class))).thenReturn(modifiee);
        
        final TypeProduit metier = fabriquerTypeProduit(nouveauLibelle, ID_1);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        final TypeProduit resultat = this.service.update(metier);
        
        /* ASSERT */
        /* Garantit que this.service.update(metier)
         * - effectue bien une modification dans le stockage
         * - retourne l'objet persistant modifié
         * - préserve exactement la casse du nouveau libellé demandé.
         */
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdTypeProduit()).isEqualTo(ID_1);
        assertThat(resultat.getTypeProduit()).isEqualTo(nouveauLibelle);

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon identifiant via findById(...).
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);

        /* Garantit qu'une sauvegarde a bien été déclenchée,
         * ce qui prouve qu'une modification effective
         * a été appliquée dans le stockage.
         */
        verify(this.typeProduitDaoJPA).save(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que update(DAO save retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE}</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>déclenche une tentative de sauvegarde via {@code save(...)}</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(DAO save retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testUpdateSauvegardeNull() {
    	
        /* ARRANGE :
         * configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie d'abord :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_1 via findById(...),
         * alors le DAO mocké avec Mockito devra répondre
         * l'objet persistant actuellement stocké".
         *
         * On simule donc volontairement un objet persistant courant
         * portant encore le libellé VETEMENT,
         * afin d'atteindre réellement la phase de sauvegarde.
         */
        final TypeProduitJPA persistee = fabriquerTypeProduitJPA(VETEMENT, ID_1);
        
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(persistee));

        /* La formule when(...).thenReturn(...) signifie ensuite :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * via save(...),
         * alors le DAO mocké avec Mockito devra répondre null".
         *
         * On simule donc volontairement une anomalie technique du stockage :
         * la sauvegarde est tentée,
         * mais le stockage retourne null au lieu de renvoyer
         * l'objet persistant modifié.
         */
        when(this.typeProduitDaoJPA.save(any(TypeProduitJPA.class))).thenReturn(null);
        
        final TypeProduit metier = fabriquerTypeProduit(CAMPING, ID_1);
        
        /* ACT - ASSERT */
        /* Garantit que this.service.update(metier)
         * - jette une ExceptionTechniqueGateway
         * - émet un message ERREUR_TECHNIQUE_KO_STOCKAGE
         *   lorsque le stockage retourne null sur save(...).
         */
        assertThatThrownBy(() -> this.service.update(metier))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon identifiant via findById(...).
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);

        /* Garantit qu'une tentative de sauvegarde a bien eu lieu,
         * ce qui prouve que l'échec technique observé
         * provient bien du retour null de save(...).
         */
        verify(this.typeProduitDaoJPA).save(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que update(KO DAO sur save message non nul) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>déclenche une tentative de sauvegarde via {@code save(...)}</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(KO DAO sur save message non nul) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testUpdateExceptionDAO() {
    	
        /* ARRANGE */
        final TypeProduitJPA persistee = fabriquerTypeProduitJPA(VETEMENT, ID_1);
        
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(persistee));

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * via save(...),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException portant le message MSG_BOOM".
         *
         * On simule donc volontairement une panne technique du stockage
         * au moment de la sauvegarde de l'objet modifié.
         *
         * Le but n'est pas de tester le DAO réel,
         * mais de maîtriser sa réaction
         * afin de prouver comment le service réagit
         * au cas contractuel "KO DAO sur save message non nul".
         */
        final RuntimeException causeDao = new RuntimeException(MSG_BOOM);

        when(this.typeProduitDaoJPA.save(any(TypeProduitJPA.class)))
            .thenThrow(causeDao);
        
        final TypeProduit metier = fabriquerTypeProduit(CAMPING, ID_1);
        
        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE). */
        /* Exécute une seule fois this.service.update(metier)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.update(metier));

        /* ASSERT */
        /* Garantit que this.service.update(metier)
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
        verify(this.typeProduitDaoJPA).findById(ID_1);

        /* Garantit qu'une tentative de sauvegarde a bien eu lieu,
         * ce qui prouve que l'échec technique observé
         * provient bien du save(...).
         */
        verify(this.typeProduitDaoJPA).save(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>garantit que update(KO DAO sur save message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non nul dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>déclenche une tentative de sauvegarde via {@code save(...)}</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(KO DAO sur save message null) : jette ExceptionTechniqueGateway avec message sûr non nul")
    @Test
    public void testUpdateExceptionDAOMsgNull() {
    	
        /* ARRANGE */
        final TypeProduitJPA persistee = fabriquerTypeProduitJPA(VETEMENT, ID_1);
        
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(persistee));

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * via save(...),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException sans message".
         *
         * On simule donc volontairement une panne technique du stockage
         * au moment de la sauvegarde de l'objet modifié,
         * avec un message technique d'origine null.
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.typeProduitDaoJPA.save(any(TypeProduitJPA.class)))
            .thenThrow(causeDao);
        
        final TypeProduit metier = fabriquerTypeProduit(CAMPING, ID_1);
        
        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE). */
        /* Exécute une seule fois this.service.update(metier)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.update(metier));

        /* ASSERT */
        /* Garantit que this.service.update(metier)
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

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon identifiant via findById(...).
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);

        /* Garantit qu'une tentative de sauvegarde a bien eu lieu,
         * ce qui prouve que l'échec technique observé
         * provient bien du save(...).
         */
        verify(this.typeProduitDaoJPA).save(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    // ============================= delete ===============================
    
    

    /**
     * <div>
     * <p>garantit que delete(null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNull}</li>
     * <li>émet un message
     * {@link TypeProduitGatewayIService#MESSAGE_DELETE_KO_PARAM_NULL}</li>
     * <li>n'appelle pas le DAO</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(null) : jette ExceptionAppliParamNull et n'appelle pas le DAO")
    @Test
    public void testDeleteNull() {
    	
        /* ARRANGE - ACT - ASSERT */
        /* Garantit que this.service.delete(null)
         * - jette une ExceptionAppliParamNull
         * - émet un message MESSAGE_DELETE_KO_PARAM_NULL.
         */
        assertThatThrownBy(() -> this.service.delete(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_DELETE_KO_PARAM_NULL);

        /* Garantit que le DAO mocké n'a pas été appelé. */
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(ID null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliParamNonPersistent}</li>
     * <li>émet un message
     * {@link TypeProduitGatewayIService#MESSAGE_DELETE_KO_ID_NULL}</li>
     * <li>n'appelle pas le DAO</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(ID null) : jette ExceptionAppliParamNonPersistent et n'appelle pas le DAO")
    @Test
    public void testDeleteIdNull() {
    	
        /* ARRANGE */
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, null);

        /* ACT - ASSERT */
        /* Garantit que this.service.delete(metier)
         * - jette une ExceptionAppliParamNonPersistent
         * - émet un message MESSAGE_DELETE_KO_ID_NULL.
         */
        assertThatThrownBy(() -> this.service.delete(metier))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_DELETE_KO_ID_NULL);

        /* Garantit que le DAO mocké n'a pas été appelé. */
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(findById retourne null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_KO_STOCKAGE}</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>ne déclenche aucune suppression</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(findById retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE et ne supprime pas")
    @Test
    public void testDeleteFindByIdDAORetourneNull() {
    	
        /* ARRANGE */
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_1 via findById(...),
         * alors le DAO mocké avec Mockito devra répondre null".
         *
         * On simule donc volontairement un stockage
         * qui retourne null au lieu d'un Optional<TypeProduitJPA>,
         * ce qui doit être interprété comme une anomalie technique
         * de stockage.
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(null);

        /* ACT - ASSERT */
        /* Garantit que this.service.delete(metier)
         * - jette une ExceptionTechniqueGateway
         * - émet un message ERREUR_TECHNIQUE_KO_STOCKAGE
         *   lorsque le stockage retourne null sur findById(...).
         */
        assertThatThrownBy(() -> this.service.delete(metier))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon identifiant via findById(...).
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);

        /* Garantit qu'aucune suppression n'a été déclenchée,
         * puisque le chargement de l'objet persistant a échoué
         * avant toute destruction.
         */
        verify(this.typeProduitDaoJPA, never()).delete(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(KO DAO sur findById message non nul) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>ne déclenche aucune suppression</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(KO DAO sur findById message non nul) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testDeleteExceptionDAOFindById() {
    	
        /* ARRANGE */
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_1 via findById(...),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException portant le message MSG_BOOM".
         *
         * On simule donc volontairement une panne technique du stockage
         * pendant le chargement de l'objet persistant à supprimer.
         */
        final RuntimeException causeDao = new RuntimeException(MSG_BOOM);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenThrow(causeDao);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE). */
        /* Exécute une seule fois this.service.delete(metier)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.delete(metier));

        /* ASSERT */
        /* Garantit que this.service.delete(metier)
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
        verify(this.typeProduitDaoJPA).findById(ID_1);

        /* Garantit qu'aucune suppression n'a été déclenchée,
         * puisque l'échec technique est survenu
         * avant toute destruction.
         */
        verify(this.typeProduitDaoJPA, never()).delete(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(KO DAO sur findById message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non nul dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>ne déclenche aucune suppression</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(KO DAO sur findById message null) : jette ExceptionTechniqueGateway avec message sûr non nul")
    @Test
    public void testDeleteExceptionDAOFindByIdMsgNull() {
    	
        /* ARRANGE */
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_1 via findById(...),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException sans message".
         *
         * On simule donc volontairement une panne technique du stockage
         * pendant le chargement de l'objet persistant à supprimer,
         * avec un message technique d'origine null.
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenThrow(causeDao);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE). */
        /* Exécute une seule fois this.service.delete(metier)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.delete(metier));

        /* ASSERT */
        /* Garantit que this.service.delete(metier)
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

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon identifiant via findById(...).
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);

        /* Garantit qu'aucune suppression n'a été déclenchée,
         * puisque l'échec technique est survenu
         * avant toute destruction.
         */
        verify(this.typeProduitDaoJPA, never()).delete(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(entity inexistante dans le stockage) :</p>
     * <ul>
     * <li>ne lève aucune exception</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>ne déclenche aucune suppression</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(entity inexistante dans le stockage) : ne fait rien et ne supprime pas")
    @Test
    public void testDeleteEntityInexistante() throws Exception {
    	
        /* ARRANGE */
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_1 via findById(...),
         * alors le DAO mocké avec Mockito devra répondre
         * Optional.empty()".
         *
         * On simule donc volontairement un stockage
         * qui ne trouve aucun TypeProduit persistant
         * pour l'identifiant demandé.
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.empty());

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        this.service.delete(metier);

        /* ASSERT */
        /* Garantit que this.service.delete(metier)
         * - ne lève aucune exception
         * - ne déclenche aucune suppression
         *   lorsque l'objet à supprimer n'existe pas dans le stockage.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);

        /* Garantit qu'aucune suppression n'a été déclenchée,
         * puisqu'aucun objet persistant n'a été trouvé
         * à supprimer dans le stockage.
         */
        verify(this.typeProduitDaoJPA, never()).delete(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(OK) :</p>
     * <ul>
     * <li>charge l'objet persistant courant via {@code findById(...)}</li>
     * <li>déclenche la suppression dans le stockage via {@code delete(...)}</li>
     * <li>supprime l'objet persistant correspondant à l'identifiant demandé</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(OK) : charge l'objet persistant et délègue la suppression au DAO")
    @Test
    public void testDeleteOK() throws Exception {
    	
        /* ARRANGE */
        final TypeProduitJPA persistee = fabriquerTypeProduitJPA(VETEMENT, ID_1);

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * avec l'identifiant ID_1 via findById(...),
         * alors le DAO mocké avec Mockito devra répondre
         * l'objet persistant actuellement stocké".
         *
         * On simule donc volontairement un objet persistant courant
         * portant le libellé VETEMENT.
         */
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(persistee));

        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        this.service.delete(metier);

        /* ASSERT */
        /* Garantit que this.service.delete(metier)
         * - charge bien l'objet persistant courant
         * - délègue ensuite sa suppression au DAO.
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.typeProduitDaoJPA).delete(persistee);
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(KO DAO sur delete message non nul) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>déclenche une tentative de suppression via {@code delete(...)}</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(KO DAO sur delete message non nul) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testDeleteExceptionDAO() {
    	
        /* ARRANGE */
        final TypeProduitJPA persistee = fabriquerTypeProduitJPA(VETEMENT, ID_1);

        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(persistee));

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule doThrow(...).when(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * via delete(...),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException portant le message MSG_BOOM".
         *
         * On simule donc volontairement une panne technique du stockage
         * au moment de la destruction de l'objet persistant.
         */
        final RuntimeException causeDao = new RuntimeException(MSG_BOOM);

        org.mockito.Mockito.doThrow(causeDao)
            .when(this.typeProduitDaoJPA).delete(any(TypeProduitJPA.class));

        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when / doThrow du ARRANGE). */
        /* Exécute une seule fois this.service.delete(metier)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.delete(metier));

        /* ASSERT */
        /* Garantit que this.service.delete(metier)
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
        verify(this.typeProduitDaoJPA).findById(ID_1);

        /* Garantit qu'une tentative de suppression a bien eu lieu,
         * ce qui prouve que l'échec technique observé
         * provient bien du delete(...).
         */
        verify(this.typeProduitDaoJPA).delete(persistee);
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que delete(KO DAO sur delete message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non nul dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois via {@code findById(...)}</li>
     * <li>déclenche une tentative de suppression via {@code delete(...)}</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(KO DAO sur delete message null) : jette ExceptionTechniqueGateway avec message sûr non nul")
    @Test
    public void testDeleteExceptionDAOMsgNull() {
    	
        /* ARRANGE */
        final TypeProduitJPA persistee = fabriquerTypeProduitJPA(VETEMENT, ID_1);

        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(persistee));

        /* Configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule doThrow(...).when(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * via delete(...),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException sans message".
         *
         * On simule donc volontairement une panne technique du stockage
         * au moment de la destruction de l'objet persistant,
         * avec un message technique d'origine null.
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        org.mockito.Mockito.doThrow(causeDao)
            .when(this.typeProduitDaoJPA).delete(any(TypeProduitJPA.class));

        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when / doThrow du ARRANGE). */
        /* Exécute une seule fois this.service.delete(metier)
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.delete(metier));

        /* ASSERT */
        /* Garantit que this.service.delete(metier)
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

        /* Garantit que le DAO mocké a bien été appelé une fois
         * avec le bon identifiant via findById(...).
         */
        verify(this.typeProduitDaoJPA).findById(ID_1);

        /* Garantit qu'une tentative de suppression a bien eu lieu,
         * ce qui prouve que l'échec technique observé
         * provient bien du delete(...).
         */
        verify(this.typeProduitDaoJPA).delete(persistee);
        
    } // __________________________________________________________________
    
    

    // ============================== Count ===============================
    
    

    /**
     * <div>
     * <p>garantit que count() en scénario nominal :</p>
     * <ul>
     * <li>délègue le comptage au DAO via {@code count()}</li>
     * <li>retourne la valeur de comptage renvoyée par le stockage</li>
     * <li>appelle le DAO une fois</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_COUNT)
    @DisplayName("count() nominal : délègue au DAO et retourne la valeur de comptage")
    @Test
    public void testCount() throws Exception {
    	
        /* ARRANGE :
         * configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * via count(),
         * alors le DAO mocké avec Mockito devra répondre TOTAL_10".
         *
         * On simule donc volontairement un stockage
         * contenant TOTAL_10 enregistrements.
         */
        when(this.typeProduitDaoJPA.count()).thenReturn(TOTAL_10);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        final long resultat = this.service.count();

        /* ASSERT */
        /* Garantit que this.service.count()
         * - délègue le comptage au DAO
         * - retourne la valeur de comptage renvoyée par le stockage.
         */
        assertThat(resultat).isEqualTo(TOTAL_10);

        /* Garantit que le DAO mocké a bien été appelé une fois
         * via la méthode count().
         */
        verify(this.typeProduitDaoJPA).count();
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que count() sur stockage vide :</p>
     * <ul>
     * <li>délègue le comptage au DAO via {@code count()}</li>
     * <li>retourne {@code 0}</li>
     * <li>appelle le DAO une fois</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_COUNT)
    @DisplayName("count() stockage vide : délègue au DAO et retourne 0")
    @Test
    public void testCountZero() throws Exception {
    	
        /* ARRANGE :
         * configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenReturn(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * via count(),
         * alors le DAO mocké avec Mockito devra répondre TOTAL_0".
         *
         * On simule donc volontairement un stockage vide.
         */
        when(this.typeProduitDaoJPA.count()).thenReturn(TOTAL_0);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE).
         */
        final long resultat = this.service.count();

        /* ASSERT */
        /* Garantit que this.service.count()
         * - délègue le comptage au DAO
         * - retourne 0 lorsque le stockage est vide.
         */
        assertThat(resultat).isEqualTo(TOTAL_0);

        /* Garantit que le DAO mocké a bien été appelé une fois
         * via la méthode count().
         */
        verify(this.typeProduitDaoJPA).count();
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que count(KO DAO message non nul) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois via {@code count()}</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_COUNT)
    @DisplayName("count(KO DAO message non nul) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testCountExceptionDAO() {
    	
        /* ARRANGE :
         * configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * via count(),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException portant le message MSG_BOOM".
         *
         * On simule donc volontairement une panne technique du stockage
         * pendant le comptage.
         */
        final RuntimeException causeDao = new RuntimeException(MSG_BOOM);

        when(this.typeProduitDaoJPA.count()).thenThrow(causeDao);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE). */
        /* Exécute une seule fois this.service.count()
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.count());

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

        /* Garantit que le DAO mocké a bien été appelé une fois
         * via la méthode count().
         */
        verify(this.typeProduitDaoJPA).count();
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que count(KO DAO message null) :</p>
     * <ul>
     * <li>jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>émet un message sûr non nul dérivé de l'exception technique</li>
     * <li>propage comme cause l'exception technique d'origine</li>
     * <li>appelle le DAO une fois via {@code count()}</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_COUNT)
    @DisplayName("count(KO DAO message null) : jette ExceptionTechniqueGateway avec message sûr non nul")
    @Test
    public void testCountExceptionDAOMsgNull() {
    	
        /* ARRANGE :
         * configure ici le comportement du DAO mocké avec Mockito.
         *
         * La formule when(...).thenThrow(...) signifie :
         * "si, pendant le test, le service appelle le DAO mocké avec Mockito
         * via count(),
         * alors le DAO mocké avec Mockito devra lancer
         * une RuntimeException sans message".
         *
         * On simule donc volontairement une panne technique du stockage
         * pendant le comptage,
         * avec un message technique d'origine null.
         */
        final RuntimeException causeDao = new RuntimeException((String) null);

        when(this.typeProduitDaoJPA.count()).thenThrow(causeDao);

        /* ACT */
        /* Sollicite la méthode voulue du SERVICE GATEWAY à tester
         * dans les conditions voulues par le Mock (when du ARRANGE). */
        /* Exécute une seule fois this.service.count()
         * et capture l'exception réellement levée,
         * afin de contrôler ensuite son type, son message et sa cause.
         */
        final Throwable throwable
            = org.assertj.core.api.Assertions.catchThrowable(
                    () -> this.service.count());

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

        /* Garantit que le DAO mocké a bien été appelé une fois
         * via la méthode count().
         */
        verify(this.typeProduitDaoJPA).count();
        
    } // __________________________________________________________________
    


    // ============================== TRIS ================================
    
    

    /**
     * <div>
     * <p>garantit que convertirRequetePageEnPageable(null) :</p>
     * <ul>
     * <li>ne lève aucune exception</li>
     * <li>retourne un {@link Pageable} non null</li>
     * <li>utilise la pagination par défaut</li>
     * <li>ne force aucun tri</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_TRIS)
    @DisplayName("convertirRequetePageEnPageable(null) : retourne un Pageable par défaut non trié")
    @Test
    public void testConvertirRequetePageEnPageableNull() throws Exception {
    	
        /* ACT */
        /* Sollicite la méthode privée à tester
         * avec une requête d'entrée null.
         */
        @SuppressWarnings(UNCHECKED)
        final Pageable pageable = (Pageable) invokePrivateMethod(
                this.service,
                CONV_REQ_PAGE,
                new Class[]{RequetePage.class},
                new Object[]{null});

        /* ASSERT */
        /* Garantit que la méthode privée :
         * - retourne un Pageable non null
         * - utilise la pagination par défaut
         * - ne force aucun tri
         *   lorsque la requête vaut null.
         */
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(pageable.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(pageable.getSort().isUnsorted()).isTrue();
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que convertirRequetePageEnPageable(tris null) :</p>
     * <ul>
     * <li>retourne un {@link Pageable} non null</li>
     * <li>conserve la page demandée</li>
     * <li>conserve la taille demandée</li>
     * <li>retourne un {@link Pageable} non trié</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_TRIS)
    @DisplayName("convertirRequetePageEnPageable(tris null) : retourne un Pageable non trié")
    @Test
    public void testConvertirRequetePageEnPageableTrisNull() throws Exception {
    	
        /* ARRANGE */
        final RequetePage requete = new RequetePage(1, 5, null);

        /* ACT */
        /* Sollicite la méthode privée à tester
         * avec une requête paginée
         * dont la liste des tris vaut null.
         */
        @SuppressWarnings(UNCHECKED)
        final Pageable pageable = (Pageable) invokePrivateMethod(
                this.service,
                CONV_REQ_PAGE,
                new Class[]{RequetePage.class},
                new Object[]{requete});

        /* ASSERT */
        /* Garantit que la méthode privée :
         * - conserve la page demandée
         * - conserve la taille demandée
         * - ne force aucun tri
         *   lorsque la liste des tris vaut null.
         */
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().isUnsorted()).isTrue();
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que convertirRequetePageEnPageable(tris vides) :</p>
     * <ul>
     * <li>retourne un {@link Pageable} non null</li>
     * <li>conserve la page demandée</li>
     * <li>conserve la taille demandée</li>
     * <li>retourne un {@link Pageable} non trié</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_TRIS)
    @DisplayName("convertirRequetePageEnPageable(tris vides) : retourne un Pageable non trié")
    @Test
    public void testConvertirRequetePageEnPageableTrisVides() throws Exception {
    	
        /* ARRANGE */
        final RequetePage requete = new RequetePage(1, 5, Collections.emptyList());

        /* ACT */
        /* Sollicite la méthode privée à tester
         * avec une requête paginée
         * dont la liste des tris est vide.
         */
        @SuppressWarnings(UNCHECKED)
        final Pageable pageable = (Pageable) invokePrivateMethod(
                this.service,
                CONV_REQ_PAGE,
                new Class[]{RequetePage.class},
                new Object[]{requete});

        /* ASSERT */
        /* Garantit que la méthode privée :
         * - conserve la page demandée
         * - conserve la taille demandée
         * - ne force aucun tri
         *   lorsque la liste des tris est vide.
         */
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().isUnsorted()).isTrue();
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que convertirRequetePageEnPageable(tris valides) :</p>
     * <ul>
     * <li>retourne un {@link Pageable} non null</li>
     * <li>conserve la page demandée</li>
     * <li>conserve la taille demandée</li>
     * <li>convertit les {@link TriSpec} valides en {@link Sort} Spring</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_TRIS)
    @DisplayName("convertirRequetePageEnPageable(tris valides) : convertit les TriSpec en Sort Spring")
    @Test
    public void testConvertirRequetePageEnPageableTrisValides() throws Exception {
    	
        /* ARRANGE :
         * prépare une liste de tris valides,
         * afin de prouver leur conversion
         * en tri Spring Data.
         */
        final List<TriSpec> tris = new ArrayList<TriSpec>();
        tris.add(new TriSpec(PROP_TYPEPRODUIT, DirectionTri.ASC));
        tris.add(new TriSpec(PROP_IDTYPEPRODUIT, DirectionTri.DESC));

        final RequetePage requete = new RequetePage(1, 5, tris);

        /* ACT */
        /* Sollicite la méthode privée à tester
         * avec une requête contenant
         * des tris valides.
         */
        @SuppressWarnings(UNCHECKED)
        final Pageable pageable = (Pageable) invokePrivateMethod(
                this.service,
                CONV_REQ_PAGE,
                new Class[]{RequetePage.class},
                new Object[]{requete});

        /* ASSERT */
        /* Garantit que la méthode privée :
         * - conserve la page demandée
         * - conserve la taille demandée
         * - convertit les TriSpec valides
         *   en Sort Spring cohérent.
         */
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().isSorted()).isTrue();
        assertThat(pageable.getSort().getOrderFor(PROP_TYPEPRODUIT)).isNotNull();
        assertThat(pageable.getSort().getOrderFor(PROP_TYPEPRODUIT).getDirection())
            .isEqualTo(Sort.Direction.ASC);
        assertThat(pageable.getSort().getOrderFor(PROP_IDTYPEPRODUIT)).isNotNull();
        assertThat(pageable.getSort().getOrderFor(PROP_IDTYPEPRODUIT).getDirection())
            .isEqualTo(Sort.Direction.DESC);
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que convertirRequetePageEnPageable(tris invalides) :</p>
     * <ul>
     * <li>retourne un {@link Pageable} non null</li>
     * <li>ignore les tris invalides</li>
     * <li>conserve uniquement les tris exploitables</li>
     * <li>retourne un {@link Sort} cohérent avec les seuls tris valides</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_TRIS)
    @DisplayName("convertirRequetePageEnPageable(tris invalides) : ignore les tris invalides et conserve les tris valides")
    @Test
    public void testConvertirRequetePageEnPageableTrisInvalides() throws Exception {
    	
        /* ARRANGE :
         * prépare une liste de tris mêlant :
         * - une entrée null
         * - une propriété blanche
         * - un tri valide.
         *
         * Le but est de prouver que la méthode privée
         * ignore les tris inutilisables
         * et ne conserve que les tris réellement exploitables.
         */
        final List<TriSpec> tris = new ArrayList<TriSpec>();
        tris.add(null);
        tris.add(new TriSpec(BLANK, DirectionTri.ASC));
        tris.add(new TriSpec(PROP_TYPEPRODUIT, DirectionTri.ASC));

        final RequetePage requete = new RequetePage(1, 5, tris);

        /* ACT */
        /* Sollicite la méthode privée à tester
         * avec une requête contenant
         * des tris invalides et valides.
         */
        @SuppressWarnings(UNCHECKED)
        final Pageable pageable = (Pageable) invokePrivateMethod(
                this.service,
                CONV_REQ_PAGE,
                new Class[]{RequetePage.class},
                new Object[]{requete});

        /* ASSERT */
        /* Garantit que la méthode privée :
         * - conserve la page demandée
         * - conserve la taille demandée
         * - ignore les tris invalides
         * - ne garde que le tri valide exploitable.
         */
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().isSorted()).isTrue();
        assertThat(pageable.getSort().getOrderFor(PROP_TYPEPRODUIT)).isNotNull();
        assertThat(pageable.getSort().getOrderFor(PROP_TYPEPRODUIT).getDirection())
            .isEqualTo(Sort.Direction.ASC);
        
    } // __________________________________________________________________
    
    

    // ========================= DEDOUBLONNAGE =============================

    
    
    /**
     * <div>
     * <p>garantit que filtrerTrierDedoublonner(null) :</p>
     * <ul>
     * <li>ne lève aucune exception</li>
     * <li>retourne une liste non null</li>
     * <li>retourne une liste vide</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DEDOUBLONNAGE)
    @DisplayName("filtrerTrierDedoublonner(null) : retourne une liste vide non null")
    @Test
    public void testFiltrerTrierDedoublonnerNull() throws Exception {
    	
        /* ACT */
        /* Sollicite la méthode privée à tester
         * avec une collection d'entrée null.
         */
        @SuppressWarnings(UNCHECKED)
        final List<TypeProduit> resultat
            = (List<TypeProduit>) invokePrivateMethod(
                    this.service,
                    FILTRERTRIER,
                    new Class[]{List.class},
                    new Object[]{null});

        /* ASSERT */
        /* Garantit que la méthode privée :
         * - ne propage pas de null en sortie
         * - retourne une liste vide non null
         *   lorsque l'entrée vaut null.
         */
        assertThat(resultat).isNotNull().isEmpty();
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que filtrerTrierDedoublonner(vide) :</p>
     * <ul>
     * <li>ne lève aucune exception</li>
     * <li>retourne une liste non null</li>
     * <li>retourne une liste vide</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DEDOUBLONNAGE)
    @DisplayName("filtrerTrierDedoublonner(vide) : retourne une liste vide non null")
    @Test
    public void testFiltrerTrierDedoublonnerVide() throws Exception {
    	
        /* ACT */
        /* Sollicite la méthode privée à tester
         * avec une collection d'entrée vide.
         */
        @SuppressWarnings(UNCHECKED)
        final List<TypeProduit> resultat
            = (List<TypeProduit>) invokePrivateMethod(
                    this.service,
                    FILTRERTRIER,
                    new Class[]{List.class},
                    new Object[]{Collections.emptyList()});

        /* ASSERT */
        /* Garantit que la méthode privée :
         * - retourne une liste non null
         * - retourne une liste vide
         *   lorsque l'entrée est vide.
         */
        assertThat(resultat).isNotNull().isEmpty();
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que filtrerTrierDedoublonner(avec nulls) :</p>
     * <ul>
     * <li>retire les éléments null</li>
     * <li>retourne uniquement les objets métier convertissables</li>
     * <li>retourne une liste triée par libellé</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DEDOUBLONNAGE)
    @DisplayName("filtrerTrierDedoublonner(avec nulls) : filtre les nulls et trie le résultat")
    @Test
    public void testFiltrerTrierDedoublonnerAvecNulls() throws Exception {
    	
        /* ARRANGE :
         * prépare volontairement une collection "sale" :
         * - deux éléments null
         * - deux entités persistantes valides.
         *
         * Le but est de prouver que la méthode privée
         * retire les null
         * avant de convertir puis trier le résultat métier.
         */
        final List<TypeProduitJPA> entities = Arrays.asList(
                null,
                fabriquerTypeProduitJPA(VETEMENT, ID_1),
                null,
                fabriquerTypeProduitJPA(OUTILLAGE, ID_2));

        /* ACT */
        /* Sollicite la méthode privée à tester
         * dans les conditions préparées ci-dessus.
         */
        @SuppressWarnings(UNCHECKED)
        final List<TypeProduit> resultat
            = (List<TypeProduit>) invokePrivateMethod(
                    this.service,
                    FILTRERTRIER,
                    new Class[]{List.class},
                    new Object[]{entities});

        /* ASSERT */
        /* Garantit que la méthode privée :
         * - filtre les éléments null
         * - conserve uniquement les objets métier convertissables
         * - trie les libellés par ordre alphabétique.
         */
        assertThat(resultat).isNotNull().hasSize(2);
        assertThat(resultat)
            .extracting(TypeProduit::getTypeProduit)
            .containsExactly(OUTILLAGE, VETEMENT);
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que filtrerTrierDedoublonner(case-insensitive) :</p>
     * <ul>
     * <li>dédoublonne les libellés au sens métier</li>
     * <li>ne tient pas compte de la casse</li>
     * <li>conserve une seule occurrence métier par libellé</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DEDOUBLONNAGE)
    @DisplayName("filtrerTrierDedoublonner(case-insensitive) : dédoublonne sans tenir compte de la casse")
    @Test
    public void testFiltrerTrierDedoublonnerCaseInsensitive() throws Exception {
    	
        /* ARRANGE :
         * prépare deux entités représentant le même libellé métier
         * à la casse près,
         * plus une troisième entité distincte.
         *
         * Le but est de prouver que le dédoublonnage
         * se fait bien au sens métier,
         * sans tenir compte de la casse.
         */
        final List<TypeProduitJPA> entities = Arrays.asList(
                fabriquerTypeProduitJPA(VETEMENT.toUpperCase(Locale.ROOT), ID_1),
                fabriquerTypeProduitJPA(VETEMENT, ID_2),
                fabriquerTypeProduitJPA(OUTILLAGE.toUpperCase(Locale.ROOT), ID_3));

        /* ACT */
        /* Sollicite la méthode privée à tester
         * dans les conditions préparées ci-dessus.
         */
        @SuppressWarnings(UNCHECKED)
        final List<TypeProduit> resultat
            = (List<TypeProduit>) invokePrivateMethod(
                    this.service,
                    FILTRERTRIER,
                    new Class[]{List.class},
                    new Object[]{entities});

        /* ASSERT */
        /* Garantit que la méthode privée :
         * - ne conserve qu'une seule occurrence métier
         *   de chaque libellé
         * - applique ce dédoublonnage
         *   sans tenir compte de la casse.
         *
         * La normalisation en lowerCase(Locale.ROOT)
         * permet ici de prouver directement
         * l'unicité métier,
         * sans imposer artificiellement
         * la casse exacte conservée en sortie.
         */
        assertThat(resultat).isNotNull().hasSize(2);
        assertThat(resultat)
            .extracting(typeProduit -> typeProduit.getTypeProduit().toLowerCase(Locale.ROOT))
            .containsExactlyInAnyOrder(VETEMENT, OUTILLAGE);
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que filtrerTrierDedoublonner(tri) :</p>
     * <ul>
     * <li>retourne les objets métier triés par libellé</li>
     * <li>respecte l'ordre alphabétique attendu</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_DEDOUBLONNAGE)
    @DisplayName("filtrerTrierDedoublonner(tri) : trie les objets métier par libellé")
    @Test
    public void testFiltrerTrierDedoublonnerTri() throws Exception {
    	
        /* ARRANGE :
         * prépare des entités valides
         * volontairement non triées.
         *
         * Le but est de prouver
         * l'ordre final imposé par la méthode privée.
         */
        final List<TypeProduitJPA> entities = Arrays.asList(
                fabriquerTypeProduitJPA(OUTILLAGE, ID_2),
                fabriquerTypeProduitJPA(VETEMENT, ID_1),
                fabriquerTypeProduitJPA(CAMPING, ID_3));

        /* ACT */
        /* Sollicite la méthode privée à tester
         * dans les conditions préparées ci-dessus.
         */
        @SuppressWarnings(UNCHECKED)
        final List<TypeProduit> resultat
            = (List<TypeProduit>) invokePrivateMethod(
                    this.service,
                    FILTRERTRIER,
                    new Class[]{List.class},
                    new Object[]{entities});

        /* ASSERT */
        /* Garantit que la méthode privée :
         * - retourne bien trois objets métier
         * - les restitue triés par libellé
         *   selon l'ordre alphabétique attendu.
         */
        assertThat(resultat).isNotNull().hasSize(3);
        assertThat(resultat)
            .extracting(TypeProduit::getTypeProduit)
            .containsExactly(CAMPING, OUTILLAGE, VETEMENT);
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que appliquerModifications(null persistant ou null métier) :</p>
     * <ul>
     * <li>retourne {@code false}</li>
     * <li>n'applique aucune modification</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("appliquerModifications(null persistant ou null métier) : retourne false")
    @Test
    public void testAppliquerModificationsAvecNull() throws Exception {
    	
        /* ARRANGE */
        final TypeProduitJPA entity = fabriquerTypeProduitJPA(VETEMENT, ID_1);
        final TypeProduit metier = fabriquerTypeProduit(CAMPING, ID_1);

        /* ACT */
        /* Sollicite la méthode privée à tester
         * dans ses deux variantes null :
         * - persistant null
         * - métier null.
         */
        final boolean resultatNullPersistant
            = (boolean) invokePrivateMethod(
                    this.service,
                    APPLIQUER_MODIFS,
                    new Class[]{TypeProduitJPA.class, TypeProduit.class},
                    new Object[]{null, metier});

        final boolean resultatNullMetier
            = (boolean) invokePrivateMethod(
                    this.service,
                    APPLIQUER_MODIFS,
                    new Class[]{TypeProduitJPA.class, TypeProduit.class},
                    new Object[]{entity, null});

        /* ASSERT */
        /* Garantit que la méthode privée :
         * - refuse d'appliquer une modification
         *   dès qu'un des paramètres vaut null
         * - retourne alors false.
         */
        assertThat(resultatNullPersistant).isFalse();
        assertThat(resultatNullMetier).isFalse();
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que appliquerModifications(sans modification) :</p>
     * <ul>
     * <li>retourne {@code false}</li>
     * <li>laisse l'entité persistante inchangée</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("appliquerModifications(sans modification) : retourne false et laisse l'entité inchangée")
    @Test
    public void testAppliquerModificationsSansModification() throws Exception {
    	
        /* ARRANGE */
        final TypeProduitJPA entity = fabriquerTypeProduitJPA(VETEMENT, ID_1);
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);

        /* ACT */
        /* Sollicite la méthode privée avec deux libellés identiques,
         * afin de prouver qu'aucune modification
         * n'est à appliquer.
         */
        final boolean resultat
            = (boolean) invokePrivateMethod(
                    this.service,
                    APPLIQUER_MODIFS,
                    new Class[]{TypeProduitJPA.class, TypeProduit.class},
                    new Object[]{entity, metier});

        /* ASSERT */
        /* Garantit que la méthode privée :
         * - retourne false
         * - ne modifie pas l'entité persistante
         *   lorsque les libellés sont identiques.
         */
        assertThat(resultat).isFalse();
        assertThat(entity.getTypeProduit()).isEqualTo(VETEMENT);
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que appliquerModifications(avec modification) :</p>
     * <ul>
     * <li>retourne {@code true}</li>
     * <li>modifie l'entité persistante</li>
     * <li>applique le nouveau libellé métier</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("appliquerModifications(avec modification) : retourne true et modifie l'entité")
    @Test
    public void testAppliquerModificationsAvecModification() throws Exception {
    	
        /* ARRANGE */
        final TypeProduitJPA entity = fabriquerTypeProduitJPA(VETEMENT, ID_1);
        final TypeProduit metier = fabriquerTypeProduit(CAMPING, ID_1);

        /* ACT */
        /* Sollicite la méthode privée avec deux libellés différents,
         * afin de prouver qu'une modification réelle
         * est appliquée à l'entité persistante.
         */
        final boolean resultat
            = (boolean) invokePrivateMethod(
                    this.service,
                    APPLIQUER_MODIFS,
                    new Class[]{TypeProduitJPA.class, TypeProduit.class},
                    new Object[]{entity, metier});

        /* ASSERT */
        /* Garantit que la méthode privée :
         * - retourne true
         * - applique effectivement le nouveau libellé
         *   à l'entité persistante.
         */
        assertThat(resultat).isTrue();
        assertThat(entity.getTypeProduit()).isEqualTo(CAMPING);
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que safeEquals(avec nulls) :</p>
     * <ul>
     * <li>gère correctement les paramètres null</li>
     * <li>retourne {@code true} pour deux null</li>
     * <li>retourne {@code false} si un seul paramètre vaut null</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("safeEquals(avec nulls) : gère correctement les paramètres null")
    @Test
    public void testSafeEqualsAvecNull() throws Exception {
    	
        /* ACT */
        /* Sollicite la méthode privée
         * sur les trois variantes pertinentes avec null :
         * - null / null
         * - valeur / null
         * - null / valeur.
         */
        final boolean resultat1
            = (boolean) invokePrivateMethod(
                    this.service,
                    SAFE_EQUALS,
                    new Class[]{Object.class, Object.class},
                    new Object[]{null, null});

        final boolean resultat2
            = (boolean) invokePrivateMethod(
                    this.service,
                    SAFE_EQUALS,
                    new Class[]{Object.class, Object.class},
                    new Object[]{VETEMENT, null});

        final boolean resultat3
            = (boolean) invokePrivateMethod(
                    this.service,
                    SAFE_EQUALS,
                    new Class[]{Object.class, Object.class},
                    new Object[]{null, VETEMENT});

        /* ASSERT */
        /* Garantit que la méthode privée :
         * - retourne true si les deux paramètres valent null
         * - retourne false si un seul paramètre vaut null.
         */
        assertThat(resultat1).isTrue();
        assertThat(resultat2).isFalse();
        assertThat(resultat3).isFalse();
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que safeEquals(objets égaux) :</p>
     * <ul>
     * <li>retourne {@code true}</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("safeEquals(objets égaux) : retourne true")
    @Test
    public void testSafeEqualsAvecObjetsEgaux() throws Exception {
    	
        /* ACT */
        /* Sollicite la méthode privée
         * avec deux objets égaux.
         */
        final boolean resultat
            = (boolean) invokePrivateMethod(
                    this.service,
                    SAFE_EQUALS,
                    new Class[]{Object.class, Object.class},
                    new Object[]{VETEMENT, VETEMENT});

        /* ASSERT */
        /* Garantit que la méthode privée
         * retourne true
         * lorsque les deux objets sont égaux.
         */
        assertThat(resultat).isTrue();
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que safeEquals(objets différents) :</p>
     * <ul>
     * <li>retourne {@code false}</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("safeEquals(objets différents) : retourne false")
    @Test
    public void testSafeEqualsAvecObjetsDifferents() throws Exception {
    	
        /* ACT */
        /* Sollicite la méthode privée
         * avec deux objets différents.
         */
        final boolean resultat
            = (boolean) invokePrivateMethod(
                    this.service,
                    SAFE_EQUALS,
                    new Class[]{Object.class, Object.class},
                    new Object[]{VETEMENT, OUTILLAGE});

        /* ASSERT */
        /* Garantit que la méthode privée
         * retourne false
         * lorsque les deux objets sont différents.
         */
        assertThat(resultat).isFalse();
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que isBlank(null) :</p>
     * <ul>
     * <li>retourne {@code true}</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("isBlank(null) : retourne true")
    @Test
    public void testIsBlankAvecNull() throws Exception {
    	
        /* ACT */
        /* Sollicite la méthode privée
         * avec un paramètre null.
         */
        final boolean resultat
            = (boolean) invokePrivateMethod(
                    this.service,
                    ISBLANK,
                    new Class[]{String.class},
                    new Object[]{null});

        /* ASSERT */
        /* Garantit que la méthode privée
         * retourne true
         * lorsque le paramètre vaut null.
         */
        assertThat(resultat).isTrue();
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que isBlank(chaîne vide) :</p>
     * <ul>
     * <li>retourne {@code true}</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("isBlank(chaîne vide) : retourne true")
    @Test
    public void testIsBlankAvecChaineVide() throws Exception {
    	
        /* ACT */
        /* Sollicite la méthode privée
         * avec une chaîne vide.
         */
        final boolean resultat
            = (boolean) invokePrivateMethod(
                    this.service,
                    ISBLANK,
                    new Class[]{String.class},
                    new Object[]{""});

        /* ASSERT */
        /* Garantit que la méthode privée
         * retourne true
         * lorsque le paramètre est vide.
         */
        assertThat(resultat).isTrue();
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que isBlank(chaîne blanche) :</p>
     * <ul>
     * <li>retourne {@code true}</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("isBlank(chaîne blanche) : retourne true")
    @Test
    public void testIsBlankAvecChaineBlanche() throws Exception {
    	
        /* ACT */
        /* Sollicite la méthode privée
         * avec une chaîne blanche.
         */
        final boolean resultat
            = (boolean) invokePrivateMethod(
                    this.service,
                    ISBLANK,
                    new Class[]{String.class},
                    new Object[]{BLANK});

        /* ASSERT */
        /* Garantit que la méthode privée
         * retourne true
         * lorsque le paramètre est blanc.
         */
        assertThat(resultat).isTrue();
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que isBlank(chaîne valide) :</p>
     * <ul>
     * <li>retourne {@code false}</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("isBlank(chaîne valide) : retourne false")
    @Test
    public void testIsBlankAvecChaineValide() throws Exception {
    	
        /* ACT */
        /* Sollicite la méthode privée
         * avec une chaîne valide.
         */
        final boolean resultat
            = (boolean) invokePrivateMethod(
                    this.service,
                    ISBLANK,
                    new Class[]{String.class},
                    new Object[]{VETEMENT});

        /* ASSERT */
        /* Garantit que la méthode privée
         * retourne false
         * lorsque le paramètre est valide.
         */
        assertThat(resultat).isFalse();
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que safeMessage(null) :</p>
     * <ul>
     * <li>retourne une chaîne vide</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("safeMessage(null) : retourne une chaîne vide")
    @Test
    public void testSafeMessageAvecNull() throws Exception {
    	
        /* ACT */
        /* Sollicite la méthode privée
         * avec un paramètre null.
         */
        final String resultat
            = (String) invokePrivateMethod(
                    this.service,
                    "safeMessage",
                    new Class[]{Object.class},
                    new Object[]{null});

        /* ASSERT */
        /* Garantit que la méthode privée
         * retourne une chaîne vide
         * lorsque le paramètre vaut null.
         */
        assertThat(resultat).isEmpty();
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que safeMessage(objet valide) :</p>
     * <ul>
     * <li>retourne la représentation textuelle de l'objet</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("safeMessage(objet valide) : retourne la représentation textuelle de l'objet")
    @Test
    public void testSafeMessageAvecObjetValide() throws Exception {
    	
        /* ACT */
        /* Sollicite la méthode privée
         * avec un objet valide.
         */
        final String resultat
            = (String) invokePrivateMethod(
                    this.service,
                    "safeMessage",
                    new Class[]{Object.class},
                    new Object[]{VETEMENT});

        /* ASSERT */
        /* Garantit que la méthode privée
         * retourne la représentation textuelle
         * de l'objet fourni.
         */
        assertThat(resultat).isEqualTo(VETEMENT);
        
    } // __________________________________________________________________



    /**
     * <div>
     * <p>garantit que safeMessage(toString null) :</p>
     * <ul>
     * <li>retourne une chaîne vide</li>
     * </ul>
     * </div>
     *
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("safeMessage(toString null) : retourne une chaîne vide")
    @Test
    public void testSafeMessageAvecToStringNull() throws Exception {
    	
        /* ARRANGE :
         * prépare un objet dont toString()
         * retourne volontairement null,
         * afin de prouver le comportement de sécurisation.
         */
        final Object objet = new Object() {
            @Override
            public String toString() {
                return null;
            }
        };

        /* ACT */
        /* Sollicite la méthode privée
         * avec cet objet volontairement atypique.
         */
        final String resultat
            = (String) invokePrivateMethod(
                    this.service,
                    "safeMessage",
                    new Class[]{Object.class},
                    new Object[]{objet});

        /* ASSERT */
        /* Garantit que la méthode privée
         * retourne une chaîne vide
         * lorsque toString() retourne null.
         */
        assertThat(resultat).isEmpty();
        
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
            final String pLibelle,
            final Long pId) {
    	
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
            final String pLibelle,
            final Long pId) {
    	
        return new TypeProduitJPA(pId, pLibelle);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Crée une page Spring à partir d'un contenu.</p>
     * </div>
     *
     * @param pContenu : List&lt;TypeProduitJPA&gt;
     * @param pPageNumber : int
     * @param pPageSize : int
     * @param pTotalElements : long
     * @return Page&lt;TypeProduitJPA&gt;
     */
    private static Page<TypeProduitJPA> creerPage(
            final List<TypeProduitJPA> pContenu,
            final int pPageNumber,
            final int pPageSize,
            final long pTotalElements) {
    	
        final Pageable pageable = PageRequest.of(pPageNumber, pPageSize);
        return new PageImpl<TypeProduitJPA>(pContenu, pageable, pTotalElements);
        
    } // __________________________________________________________________
    
    
    

    /**
     * <div>
     * <p>Appelle une méthode privée via reflection.</p>
     * </div>
     *
     * @param pObject : Object
     * @param pMethodName : String
     * @param pParameterTypes : Class&lt;?&gt;[]
     * @param pParameters : Object[]
     * @return Object
     * @throws Exception
     */
    private static Object invokePrivateMethod(
            final Object pObject,
            final String pMethodName,
            final Class<?>[] pParameterTypes,
            final Object... pParameters) throws Exception {

        final Method method = pObject.getClass()
                .getDeclaredMethod(pMethodName, pParameterTypes);
        method.setAccessible(true); // NOPMD by danyl on 02/02/2026 16:36
        return method.invoke(pObject, pParameters);
        
    } // __________________________________________________________________


    
} // FIN DE LA CLASSE TypeProduitGatewayJPAServiceMockTest.----------------