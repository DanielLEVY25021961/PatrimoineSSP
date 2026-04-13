/* ********************************************************************* */
/* ********************* TEST MOCKITO GATEWAY JPA ********************** */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.gateway.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
     * <p>DAO mocké.</p>
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
     * <p>Initialise le service GATEWAY avec un DAO mocké.</p>
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
    	 * - émet un message MESSAGE_CREER_KO_PARAM_NULL.
    	 */
        assertThatThrownBy(() -> this.service.creer(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_CREER_KO_PARAM_NULL);
        
        /* Garantit que le DAO mocké n'a pas été appelé. */
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>garantit que creer(blank) :</p>
     * <ul>
     * <li>jette une {@link ExceptionAppliLibelleBlank}</li>
     * <li>émet un
     * {@link TypeProduitGatewayIService#MESSAGE_CREER_KO_LIBELLE_BLANK}</li>
     * <li>n'appelle pas le DAO</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(blank) : jette ExceptionAppliLibelleBlank et n'appelle pas le DAO")
    @Test
    public void testCreerBlank() {
    	
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
     * <p>garantit que si DAO.save(entity) retourne null :</p>
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
    @DisplayName("creer(DAO retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testCreerDAORetourneNull() {
    	
    	/* ARRANGE :
    	 * prépare un objet métier valide pour atteindre réellement l'appel DAO.
    	 * On utilise un objet métier stockable,
    	 * afin que l'échec observé provienne bien du stockage
    	 * et non d'un contrôle préalable sur les paramètres.
    	 */
        final TypeProduit aCreer = fabriquerTypeProduit(VETEMENT, null);
        
        /* Simule un DAO qui retourne null au lieu de renvoyer l'entité sauvée :
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
    @DisplayName("creer(doublon stockage) : jette ExceptionTechniqueGateway et propage la cause")
    @Test
    public void testCreerDoublonFonctionnelRefuseParStockage() {
    	
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
    	
    	/* Simule un refus du stockage pour cause de doublon fonctionnel :
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
    public void testCreerOK() throws Exception {
    	
    	/* ARRANGE :
    	 * prépare un objet métier valide à créer,
    	 * une entité JPA simulant le retour du stockage après save(...),
    	 * et un captor pour contrôler précisément ce qui a été envoyé au DAO.
    	 */
        final TypeProduit aCreer = fabriquerTypeProduit(VETEMENT, null);
        final TypeProduitJPA persistee = fabriquerTypeProduitJPA(VETEMENT, ID_1);
        final ArgumentCaptor<TypeProduitJPA> captor
        	= ArgumentCaptor.forClass(TypeProduitJPA.class);

        /* Simule un DAO qui persiste correctement l'entité
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
    
    
    
    /**
     * <div>
     * <p>garantit que si le DAO jette une exception pendant creer(...) :</p>
     * <ul>
     * <li>le SERVICE GATEWAY JPA jette une {@link ExceptionTechniqueGateway}</li>
     * <li>émet un message commençant par
     * {@link TypeProduitGatewayIService#ERREUR_TECHNIQUE_STOCKAGE}</li>
     * <li>conserve le message technique d'origine du DAO</li>
     * <li>appelle le DAO une fois</li>
     * </ul>
     * </div>
     */
    @Tag(TAG_CREER)
    @DisplayName("creer(KO DAO) : jette ExceptionTechniqueGateway (wrap)")
    @Test
    public void testCreerKOExceptionDAO() {
    	
    	/* ARRANGE */
        final TypeProduit aCreer = fabriquerTypeProduit(VETEMENT, null);
        
        /* Simule une panne technique du DAO au moment du save :
         * le DAO mocké jette une RuntimeException portant le message MSG_BOOM. */
        when(this.typeProduitDaoJPA.save(any(TypeProduitJPA.class)))
            .thenThrow(new RuntimeException(MSG_BOOM));

        /* ACT - ASSERT */
        /* Garantit que this.service.creer(aCreer)
         * - jette une ExceptionTechniqueGateway
         * - émet un message commençant par ERREUR_TECHNIQUE_STOCKAGE
         * - conserve le message technique d'origine MSG_BOOM.
         */
        assertThatThrownBy(() -> this.service.creer(aCreer))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);
        
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
    public void testCreerKOExceptionDAOMsgNull() {
    	
    	/* ARRANGE :
    	 * prépare un objet métier valide pour atteindre réellement l'appel DAO.
    	 */
    	final TypeProduit aCreer = fabriquerTypeProduit(VETEMENT, null);
    	
    	/* Prépare une exception technique dont le message est null.
    	 * Ce cas est utile pour vérifier que safeMessage(e)
    	 * construit malgré tout un message sûr non nul.
    	 */
    	final RuntimeException causeDao = new RuntimeException((String) null);
    	
    	/* Simule une panne technique du DAO au moment du save(...).
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
    
    

    // ======================== RechercherTous ============================

    
    
    /**
     * <div>
     * <p>rechercherTous() : jette {@link ExceptionTechniqueGateway}
     * si le DAO retourne {@code null}.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous() : jette ExceptionTechniqueGateway si DAO retourne null")
    @Test
    public void testRechercherTousDAORetourneNull() {
    	
        when(this.typeProduitDaoJPA.findAll()).thenReturn(null);
        
        assertThatThrownBy(() -> this.service.rechercherTous())
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);
        verify(this.typeProduitDaoJPA).findAll();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>rechercherTous() : retourne une liste vide si le stockage est vide.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous() : retourne une liste vide si le stockage est vide")
    @Test
    public void testRechercherTousVide() throws Exception {
    	
        when(this.typeProduitDaoJPA.findAll()).thenReturn(Collections.emptyList());
        final List<TypeProduit> resultat = this.service.rechercherTous();
        
        assertThat(resultat).isNotNull().isEmpty();
        verify(this.typeProduitDaoJPA).findAll();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>rechercherTous() : filtre les null, trie et dédoublonne.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous() : filtre les null, trie et dédoublonne")
    @Test
    public void testRechercherTousOKTriDedoublonnage() throws Exception {
    	
        final List<TypeProduitJPA> contenu = Arrays.asList(
                fabriquerTypeProduitJPA(OUTILLAGE, ID_2),
                null,
                fabriquerTypeProduitJPA(VETEMENT, ID_1),
                fabriquerTypeProduitJPA(VETEMENT, ID_3),
                fabriquerTypeProduitJPA(CAMPING, ID_4));

        when(this.typeProduitDaoJPA.findAll()).thenReturn(contenu);
        final List<TypeProduit> resultat = this.service.rechercherTous();

        assertThat(resultat).isNotNull().hasSize(3);
        assertThat(resultat)
            .extracting(TypeProduit::getTypeProduit)
            .containsExactly(CAMPING, OUTILLAGE, VETEMENT);
        verify(this.typeProduitDaoJPA).findAll();
        
    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>rechercherTous(KO DAO avec message technique) :
     * encapsule en {@link ExceptionTechniqueGateway}
     * en conservant le message d'origine.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous(KO DAO) : encapsulation avec message technique")
    @Test
    public void testRechercherTousExceptionDAOMessagePreserve() {

        /* Le DAO est simulé pour lever une RuntimeException
         * contenant déjà un message technique.
         * On ne peut pas lever ExceptionTechniqueGateway directement
         * car c'est une checked exception non déclarée par findAll(). */
        when(this.typeProduitDaoJPA.findAll())
            .thenThrow(new RuntimeException(MSG_ERREUR_TECH_KO_STOCKAGE));

        /* Vérifie que le Gateway encapsule l'exception technique
         * dans une ExceptionTechniqueGateway et que le message
         * final contient à la fois :
         * - le préfixe contractuel ERREUR_TECHNIQUE_STOCKAGE
         * - le message d'origine fourni par le DAO */
        assertThatThrownBy(() -> this.service.rechercherTous())
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_ERREUR_TECH_KO_STOCKAGE);

        /* Vérifie que la méthode DAO a bien été appelée une fois. */
        verify(this.typeProduitDaoJPA).findAll();
        
    } // _________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>rechercherTous(KO DAO) :
     * wrappe en {@link ExceptionTechniqueGateway}.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("rechercherTous(KO DAO) : jette ExceptionTechniqueGateway (wrap)")
    @Test
    public void testRechercherTousExceptionDAO() {

        when(this.typeProduitDaoJPA.findAll())
            .thenThrow(new RuntimeException(MSG_BOOM));

        assertThatThrownBy(() -> this.service.rechercherTous())
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);

        verify(this.typeProduitDaoJPA).findAll();
        
    } // _________________________________________________________________

    
    
    // ================== rechercherTousParPage ===========================
    
    

    /**
     * <div>
     * <p>rechercherTousParPage(null) : applique la pagination par défaut
     * et délègue au DAO.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(null) : applique la pagination par défaut")
    @Test
    public void testRechercherTousParPageNull() throws Exception {
    	
        final List<TypeProduitJPA> contenuJPA = Arrays.asList(
                fabriquerTypeProduitJPA(VETEMENT, ID_1),
                fabriquerTypeProduitJPA(OUTILLAGE, ID_2));

        final Page<TypeProduitJPA> page = creerPage(
                contenuJPA,
                RequetePage.PAGE_DEFAUT,
                RequetePage.TAILLE_DEFAUT,
                2L);

        final ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        when(this.typeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        final ResultatPage<TypeProduit> resultat = this.service.rechercherTousParPage(null);

        verify(this.typeProduitDaoJPA).findAll(captor.capture());
        final Pageable pageable = captor.getValue();
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(pageable.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(pageable.getSort().isSorted()).isFalse();

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
     * <p>rechercherTousParPage(DAO retourne null) :
     * jette {@link ExceptionTechniqueGateway} KO_STOCKAGE.</p>
     * </div>
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(DAO retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testRechercherTousParPageDAORetourneNull() {
    	
        when(this.typeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(null);
        
        assertThatThrownBy(() -> this.service.rechercherTousParPage(new RequetePage()))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);
        verify(this.typeProduitDaoJPA).findAll(any(Pageable.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>rechercherTousParPage(content null) :
     * jette {@link ExceptionTechniqueGateway} KO_STOCKAGE.</p>
     * </div>
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(content null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testRechercherTousParPageContentNull() {
    	
        final Page<TypeProduitJPA> pageMock = org.mockito.Mockito.mock(Page.class);
        when(pageMock.getContent()).thenReturn(null);
        when(this.typeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(pageMock);

        assertThatThrownBy(() -> this.service.rechercherTousParPage(new RequetePage()))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);
        verify(this.typeProduitDaoJPA).findAll(any(Pageable.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>rechercherTousParPage(avec tri) : convertit TriSpec en Sort Spring.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(avec tri) : convertit TriSpec en Sort Spring")
    @Test
    public void testRechercherTousParPageAvecTri() throws Exception {
    	
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

        final ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        
        when(this.typeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        final ResultatPage<TypeProduit> resultat = this.service.rechercherTousParPage(requete);

        verify(this.typeProduitDaoJPA).findAll(captor.capture());
        final Pageable pageable = captor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(3);

        final Sort sort = pageable.getSort();
        assertThat(sort.isSorted()).isTrue();
        assertThat(sort.getOrderFor(PROP_TYPEPRODUIT)).isNotNull();
        assertThat(sort.getOrderFor(PROP_TYPEPRODUIT).getDirection()).isEqualTo(Sort.Direction.ASC);
        assertThat(sort.getOrderFor(PROP_IDTYPEPRODUIT)).isNotNull();
        assertThat(sort.getOrderFor(PROP_IDTYPEPRODUIT).getDirection()).isEqualTo(Sort.Direction.DESC);

        assertThat(resultat.getContent()).hasSize(1);
        assertThat(resultat.getContent().get(0).getTypeProduit()).isEqualTo(CAMPING);
        
    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>rechercherTousParPage(KO DAO) : wrappe en {@link ExceptionTechniqueGateway}.</p>
     * </div>
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(KO DAO) : jette ExceptionTechniqueGateway (wrap)")
    @Test
    public void testRechercherTousParPageExceptionDAO() {
    	
        when(this.typeProduitDaoJPA.findAll(any(Pageable.class))).thenThrow(new RuntimeException(MSG_BOOM));
        
        assertThatThrownBy(() -> this.service.rechercherTousParPage(new RequetePage()))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);
        verify(this.typeProduitDaoJPA).findAll(any(Pageable.class));
        
    } // __________________________________________________________________

    
    
    
    /**
     * <div>
     * <p>rechercherTousParPage(contenu avec nulls) :
     * filtre les nulls lors de la conversion.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_PAGINATION)
    @DisplayName("rechercherTousParPage(contenu avec nulls) : filtre les nulls")
    @Test
    public void testRechercherTousParPageContenuAvecNulls() throws Exception {

        final List<TypeProduitJPA> contenu = Arrays.asList(
            fabriquerTypeProduitJPA(VETEMENT, ID_1),
            null,
            fabriquerTypeProduitJPA(OUTILLAGE, ID_2)
        );

        final Page<TypeProduitJPA> page =
            creerPage(contenu, 0, 10, 3L);

        when(this.typeProduitDaoJPA.findAll(any(Pageable.class)))
            .thenReturn(page);

        final ResultatPage<TypeProduit> resultat =
            this.service.rechercherTousParPage(new RequetePage());

        assertThat(resultat.getContent())
            .extracting(TypeProduit::getTypeProduit)
            .containsExactly(VETEMENT, OUTILLAGE);
        
    } // __________________________________________________________________


        
    // ========================= FINDBYOBJETMETIER =========================

    
    
    /**
     * <div>
     * <p>findByObjetMetier(null) : jette {@link ExceptionAppliParamNull}
     * avec {@link TypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL}
     * et n'appelle pas le DAO.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(null) : jette ExceptionAppliParamNull et n'appelle pas le DAO")
    @Test
    public void testFindByObjetMetierNull() {
    	
        assertThatThrownBy(() -> this.service.findByObjetMetier(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL);
        
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>findByObjetMetier(libellé null) : jette {@link ExceptionAppliLibelleBlank}
     * avec {@link TypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK}
     * et n'appelle pas le DAO.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(libellé null) : jette ExceptionAppliLibelleBlank et n'appelle pas le DAO")
    @Test
    public void testFindByObjetMetierLibelleNull() {
    	
        final TypeProduit metier = fabriquerTypeProduit(null, null);
        
        assertThatThrownBy(() -> this.service.findByObjetMetier(metier))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>findByObjetMetier(libellé blank) : jette {@link ExceptionAppliLibelleBlank}
     * avec {@link TypeProduitGatewayIService#MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK}
     * et n'appelle pas le DAO.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(libellé blank) : jette ExceptionAppliLibelleBlank et n'appelle pas le DAO")
    @Test
    public void testFindByObjetMetierLibelleBlank() {
    	
        final TypeProduit metier = fabriquerTypeProduit(BLANK, null);
        
        assertThatThrownBy(() -> this.service.findByObjetMetier(metier))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK);
        
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>findByObjetMetier(non trouvé) : délègue à findByLibelle() (DAO)
     * et retourne null.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(non trouvé) : délègue DAO et retourne null")
    @Test
    public void testFindByObjetMetierNonTrouve() throws Exception {
    	
        when(this.typeProduitDaoJPA.findByTypeProduitIgnoreCase(VETEMENT)).thenReturn(null);
        
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, null);
        final TypeProduit resultat = this.service.findByObjetMetier(metier);
        assertThat(resultat).isNull();
        verify(this.typeProduitDaoJPA).findByTypeProduitIgnoreCase(VETEMENT);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>findByObjetMetier(trouvé) : délègue à findByLibelle() (DAO)
     * et retourne l'objet métier converti.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(trouvé) : délègue DAO et retourne l'objet métier converti")
    @Test
    public void testFindByObjetMetierTrouve() throws Exception {
    	
        when(this.typeProduitDaoJPA.findByTypeProduitIgnoreCase(VETEMENT))
            .thenReturn(fabriquerTypeProduitJPA(VETEMENT, ID_1));
        
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, null);
        final TypeProduit resultat = this.service.findByObjetMetier(metier);
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdTypeProduit()).isEqualTo(ID_1);
        assertThat(resultat.getTypeProduit()).isEqualTo(VETEMENT);
        verify(this.typeProduitDaoJPA).findByTypeProduitIgnoreCase(VETEMENT);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>findByObjetMetier(KO DAO) : wrappe en {@link ExceptionTechniqueGateway}.</p>
     * </div>
     */
    @Tag(TAG_FINDBYOBJETMETIER)
    @DisplayName("findByObjetMetier(KO DAO) : jette ExceptionTechniqueGateway (wrap)")
    @Test
    public void testFindByObjetMetierExceptionDAO() {
    	
        when(this.typeProduitDaoJPA.findByTypeProduitIgnoreCase(VETEMENT))
            .thenThrow(new RuntimeException(MSG_BOOM));
        
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, null);
        assertThatThrownBy(() -> this.service.findByObjetMetier(metier))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);
        verify(this.typeProduitDaoJPA).findByTypeProduitIgnoreCase(VETEMENT);
        
    } // __________________________________________________________________
    
    

    // ========================== findByLibelle ===========================

    
    
    /**
     * <div>
     * <p>findByLibelle(blank) : jette {@link ExceptionAppliLibelleBlank}.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelle(blank) : jette ExceptionAppliLibelleBlank")
    @Test
    public void testFindByLibelleBlank() {
    	
        assertThatThrownBy(() -> this.service.findByLibelle(BLANK))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK);
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>findByLibelle(non trouvé) : retourne null.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelle(non trouvé) : retourne null")
    @Test
    public void testFindByLibelleNonTrouve() throws Exception {
    	
        when(this.typeProduitDaoJPA.findByTypeProduitIgnoreCase(VETEMENT)).thenReturn(null);
        
        final TypeProduit resultat = this.service.findByLibelle(VETEMENT);
        assertThat(resultat).isNull();
        verify(this.typeProduitDaoJPA).findByTypeProduitIgnoreCase(VETEMENT);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>findByLibelle(trouvé) : retourne l'objet métier converti.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelle(trouvé) : retourne l'objet métier converti")
    @Test
    public void testFindByLibelleTrouve() throws Exception {
    	
        when(this.typeProduitDaoJPA.findByTypeProduitIgnoreCase(VETEMENT))
            .thenReturn(fabriquerTypeProduitJPA(VETEMENT, ID_1));
        
        final TypeProduit resultat = this.service.findByLibelle(VETEMENT);
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdTypeProduit()).isEqualTo(ID_1);
        assertThat(resultat.getTypeProduit()).isEqualTo(VETEMENT);
        verify(this.typeProduitDaoJPA).findByTypeProduitIgnoreCase(VETEMENT);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>findByLibelle(KO DAO) : wrappe en {@link ExceptionTechniqueGateway}.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findByLibelle(KO DAO) : jette ExceptionTechniqueGateway (wrap)")
    @Test
    public void testFindByLibelleExceptionDAO() {
    	
        when(this.typeProduitDaoJPA.findByTypeProduitIgnoreCase(VETEMENT))
            .thenThrow(new RuntimeException(MSG_BOOM));
        
        assertThatThrownBy(() -> this.service.findByLibelle(VETEMENT))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);
        verify(this.typeProduitDaoJPA).findByTypeProduitIgnoreCase(VETEMENT);
        
    } // __________________________________________________________________
    
    

    // ======================== findByLibelleRapide =======================
    
    

    /**
     * <div>
     * <p>findByLibelleRapide(null) : jette {@link ExceptionAppliParamNull}.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName("findByLibelleRapide(null) : jette ExceptionAppliParamNull")
    @Test
    public void testFindByLibelleRapideNull() {
    	
        assertThatThrownBy(() -> this.service.findByLibelleRapide(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL);
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>findByLibelleRapide(blank) : délègue findAll().</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName("findByLibelleRapide(blank) : délègue findAll()")
    @Test
    public void testFindByLibelleRapideBlankDelegueFindAll() throws Exception {
    	
        when(this.typeProduitDaoJPA.findAll()).thenReturn(Arrays.asList(
                fabriquerTypeProduitJPA(OUTILLAGE, ID_2),
                fabriquerTypeProduitJPA(VETEMENT, ID_1)));
        
        final List<TypeProduit> resultat = this.service.findByLibelleRapide(BLANK);
        assertThat(resultat).isNotNull().hasSize(2);
        verify(this.typeProduitDaoJPA).findAll();
        verify(this.typeProduitDaoJPA, never())
            .findByTypeProduitContainingIgnoreCase(any(String.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>findByLibelleRapide(non blank) : délègue containing().</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName("findByLibelleRapide(non blank) : délègue containing()")
    @Test
    public void testFindByLibelleRapideNonBlankDelegueContaining() throws Exception {
    	
        when(this.typeProduitDaoJPA.findByTypeProduitContainingIgnoreCase(RECHERCHE_ME))
            .thenReturn(Collections.singletonList(fabriquerTypeProduitJPA(VETEMENT, ID_1)));
        
        final List<TypeProduit> resultat = this.service.findByLibelleRapide(RECHERCHE_ME);
        assertThat(resultat).isNotNull().hasSize(1);
        assertThat(resultat.get(0).getTypeProduit()).isEqualTo(VETEMENT);
        verify(this.typeProduitDaoJPA).findByTypeProduitContainingIgnoreCase(RECHERCHE_ME);
        verify(this.typeProduitDaoJPA, never()).findAll();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>findByLibelleRapide(DAO retourne null) : jette KO_STOCKAGE.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName("findByLibelleRapide(DAO retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testFindByLibelleRapideDAORetourneNull() {
    	
        when(this.typeProduitDaoJPA.findByTypeProduitContainingIgnoreCase(RECHERCHE_ME))
            .thenReturn(null);
        
        assertThatThrownBy(() -> this.service.findByLibelleRapide(RECHERCHE_ME))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);
        verify(this.typeProduitDaoJPA).findByTypeProduitContainingIgnoreCase(RECHERCHE_ME);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>findByLibelleRapide(blank) : délègue findAll() ; si DAO retourne null :
     * jette {@link ExceptionTechniqueGateway} KO_STOCKAGE.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName("findByLibelleRapide(blank + DAO retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testFindByLibelleRapideBlankDAORetourneNull() {
    	
        when(this.typeProduitDaoJPA.findAll()).thenReturn(null);
        
        assertThatThrownBy(() -> this.service.findByLibelleRapide(BLANK))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);
        verify(this.typeProduitDaoJPA).findAll();
        verify(this.typeProduitDaoJPA, never())
            .findByTypeProduitContainingIgnoreCase(any(String.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>findByLibelleRapide(KO DAO) : wrappe en {@link ExceptionTechniqueGateway}.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName("findByLibelleRapide(KO DAO) : jette ExceptionTechniqueGateway (wrap)")
    @Test
    public void testFindByLibelleRapideExceptionDAO() {
    	
        when(this.typeProduitDaoJPA.findByTypeProduitContainingIgnoreCase(RECHERCHE_ME))
            .thenThrow(new RuntimeException(MSG_BOOM));
        
        assertThatThrownBy(() -> this.service.findByLibelleRapide(RECHERCHE_ME))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);
        verify(this.typeProduitDaoJPA).findByTypeProduitContainingIgnoreCase(RECHERCHE_ME);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>findByLibelleRapide(dédoublonnage) : retourne une liste sans doublons de libellés.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_DEDOUBLONNAGE)
    @DisplayName("findByLibelleRapide(dédoublonnage) : retourne une liste sans doublons de libellés")
    @Test
    public void testFindByLibelleRapideDedoublonnage() throws Exception {
    	
        final List<TypeProduitJPA> entities = Arrays.asList(
            fabriquerTypeProduitJPA(VETEMENT, ID_1),
            fabriquerTypeProduitJPA(VETEMENT, ID_3), // Doublon de libellé
            fabriquerTypeProduitJPA(OUTILLAGE, ID_2)
        );
        
        when(this.typeProduitDaoJPA.findByTypeProduitContainingIgnoreCase(RECHERCHE_ME))
            .thenReturn(entities);
        
        final List<TypeProduit> resultat = this.service.findByLibelleRapide(RECHERCHE_ME);
        assertThat(resultat).hasSize(2); // Vêtement + Outillage (dédoublonnage appliqué)
        assertThat(resultat)
            .extracting(TypeProduit::getTypeProduit)
            .containsExactlyInAnyOrder(VETEMENT, OUTILLAGE);
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>findByLibelleRapide(tri final) :
     * retourne les résultats triés par libellé.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER_RAPIDE)
    @DisplayName("findByLibelleRapide(tri final) : retourne les résultats triés")
    @Test
    public void testFindByLibelleRapideTriFinal() throws Exception {

        final List<TypeProduitJPA> entities = Arrays.asList(
            fabriquerTypeProduitJPA(OUTILLAGE, ID_2),
            fabriquerTypeProduitJPA(CAMPING, ID_1),
            fabriquerTypeProduitJPA(VETEMENT, ID_3)
        );

        when(this.typeProduitDaoJPA
            .findByTypeProduitContainingIgnoreCase(RECHERCHE_ME))
            .thenReturn(entities);

        final List<TypeProduit> resultat =
            this.service.findByLibelleRapide(RECHERCHE_ME);

        assertThat(resultat)
            .extracting(TypeProduit::getTypeProduit)
            .containsExactly(CAMPING, OUTILLAGE, VETEMENT);
        
    } // __________________________________________________________________


    
    // ============================ findById ==============================

    
    
    /**
     * <div>
     * <p>findById(null) : jette {@link ExceptionAppliParamNull}.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findById(null) : jette ExceptionAppliParamNull")
    @Test
    public void testFindByIdNull() {
    	
        assertThatThrownBy(() -> this.service.findById(null)
            ).isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_FINDBYID_KO_PARAM_NULL);
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>findById(DAO retourne null) : jette {@link ExceptionTechniqueGateway}
     * KO_STOCKAGE.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findById(DAO retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testFindByIdDAORetourneNull() {
    	
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(null);
        
        assertThatThrownBy(() -> this.service.findById(ID_1))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);
        verify(this.typeProduitDaoJPA).findById(ID_1);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>findById(non trouvé) : retourne null.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findById(non trouvé) : retourne null")
    @Test
    public void testFindByIdNonTrouve() throws Exception {
    	
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.empty());
        
        final TypeProduit resultat = this.service.findById(ID_1);
        assertThat(resultat).isNull();
        verify(this.typeProduitDaoJPA).findById(ID_1);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>findById(trouvé) : retourne l'objet métier converti.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findById(trouvé) : retourne l'objet métier converti")
    @Test
    public void testFindByIdTrouve() throws Exception {
    	
        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenReturn(Optional.of(fabriquerTypeProduitJPA(VETEMENT, ID_1)));
        
        final TypeProduit resultat = this.service.findById(ID_1);
        
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdTypeProduit()).isEqualTo(ID_1);
        assertThat(resultat.getTypeProduit()).isEqualTo(VETEMENT);
        verify(this.typeProduitDaoJPA).findById(ID_1);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>findById(ID inexistant) : retourne null.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findById(ID inexistant) : retourne null")
    @Test
    public void testFindByIdInexistant() throws Exception {
    	
        when(this.typeProduitDaoJPA.findById(ID_INEXISTANT)).thenReturn(Optional.empty());
        
        final TypeProduit resultat = this.service.findById(ID_INEXISTANT);
        
        assertThat(resultat).isNull();
        verify(this.typeProduitDaoJPA).findById(ID_INEXISTANT);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>findById(KO DAO) : wrappe en {@link ExceptionTechniqueGateway}.</p>
     * </div>
     */
    @Tag(TAG_RECHERCHER)
    @DisplayName("findById(KO DAO) : jette ExceptionTechniqueGateway (wrap)")
    @Test
    public void testFindByIdExceptionDAO() {
    	
        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenThrow(new RuntimeException(MSG_BOOM));
        
        assertThatThrownBy(() -> this.service.findById(ID_1))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);
        verify(this.typeProduitDaoJPA).findById(ID_1);
        
    } // __________________________________________________________________
    
    

    // ============================= update ===============================
    
    

    /**
     * <div>
     * <p>update(null) : jette {@link ExceptionAppliParamNull}.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(null) : jette ExceptionAppliParamNull")
    @Test
    public void testUpdateNull() {
    	
        assertThatThrownBy(() -> this.service.update(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_UPDATE_KO_PARAM_NULL);
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>update(blank) : jette {@link ExceptionAppliLibelleBlank}.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(blank) : jette ExceptionAppliLibelleBlank")
    @Test
    public void testUpdateBlank() {
    	
        final TypeProduit metier = fabriquerTypeProduit(BLANK, ID_1);
        
        assertThatThrownBy(() -> this.service.update(metier))
            .isInstanceOf(ExceptionAppliLibelleBlank.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_BLANK);
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>update(ID null) : jette {@link ExceptionAppliParamNonPersistent}
     * avec {@link TypeProduitGatewayIService#MESSAGE_UPDATE_KO_NON_PERSISTENT}
     * + libellé.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(ID null) : jette ExceptionAppliParamNonPersistent")
    @Test
    public void testUpdateIdNull() {
    	
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, null);
        
        assertThatThrownBy(() -> this.service.update(metier))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_UPDATE_KO_NON_PERSISTENT + VETEMENT);
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>update(findById retourne null) : jette {@link ExceptionTechniqueGateway}
     * KO_STOCKAGE, et ne sauvegarde pas.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(findById retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testUpdateFindByIdDAORetourneNull() {
    	
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);
        
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(null);
        assertThatThrownBy(() -> this.service.update(metier))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.typeProduitDaoJPA, never()).save(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>update(KO DAO sur findById) : wrappe en {@link ExceptionTechniqueGateway}.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(KO DAO sur findById) : jette ExceptionTechniqueGateway (wrap)")
    @Test
    public void testUpdateExceptionDAOFindById() {
    	
        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenThrow(new RuntimeException(MSG_BOOM));
        
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);
        
        assertThatThrownBy(() -> this.service.update(metier))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.typeProduitDaoJPA, never()).save(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>update(entity inexistante) : retourne null, ne sauvegarde pas.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(entity inexistante) : retourne null")
    @Test
    public void testUpdateEntityInexistante() throws Exception {
    	
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);
        
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.empty());
        
        final TypeProduit resultat = this.service.update(metier);
        
        assertThat(resultat).isNull();
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.typeProduitDaoJPA, never()).save(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>update(aucune modification) : ne sauvegarde pas.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(aucune modification) : ne déclenche pas de save()")
    @Test
    public void testUpdateAucuneModification() throws Exception {
    	
        final TypeProduitJPA persistee = fabriquerTypeProduitJPA(VETEMENT, ID_1);
        
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(persistee));
        
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);
        final TypeProduit resultat = this.service.update(metier);
        
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdTypeProduit()).isEqualTo(ID_1);
        assertThat(resultat.getTypeProduit()).isEqualTo(VETEMENT);
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.typeProduitDaoJPA, never()).save(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>update(modification) : sauvegarde et retourne modifié.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(modification) : sauvegarde via save() et retourne modifié")
    @Test
    public void testUpdateAvecModification() throws Exception {
    	
        final TypeProduitJPA persistee = fabriquerTypeProduitJPA(VETEMENT, ID_1);
        
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(persistee));
        
        final TypeProduitJPA modifiee = fabriquerTypeProduitJPA(CAMPING, ID_1);
        
        when(this.typeProduitDaoJPA.save(any(TypeProduitJPA.class))).thenReturn(modifiee);
        
        final TypeProduit metier = fabriquerTypeProduit(CAMPING, ID_1);
        final TypeProduit resultat = this.service.update(metier);
        
        assertThat(resultat).isNotNull();
        assertThat(resultat.getIdTypeProduit()).isEqualTo(ID_1);
        assertThat(resultat.getTypeProduit()).isEqualTo(CAMPING);
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.typeProduitDaoJPA).save(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>update(modification de casse) : préserve la casse du nouveau libellé.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(modification de casse) : préserve la casse du nouveau libellé")
    @Test
    public void testUpdateModificationCasse() throws Exception {
    	
        final TypeProduitJPA persistee = fabriquerTypeProduitJPA(VETEMENT, ID_1);
        
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(persistee));
        
        final String nouveauLibelle = VETEMENT.toUpperCase(Locale.getDefault());
        final TypeProduitJPA modifiee = fabriquerTypeProduitJPA(nouveauLibelle, ID_1);
        
        when(this.typeProduitDaoJPA.save(any(TypeProduitJPA.class))).thenReturn(modifiee);
        
        final TypeProduit metier = fabriquerTypeProduit(nouveauLibelle, ID_1);
        final TypeProduit resultat = this.service.update(metier);
        
        assertThat(resultat).isNotNull();
        assertThat(resultat.getTypeProduit()).isEqualTo(nouveauLibelle); // Casse préservée
        verify(this.typeProduitDaoJPA).save(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>update(DAO save retourne null) : jette KO_STOCKAGE.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(DAO save retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testUpdateSauvegardeNull() {
    	
        final TypeProduitJPA persistee = fabriquerTypeProduitJPA(VETEMENT, ID_1);
        
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(persistee));
        when(this.typeProduitDaoJPA.save(any(TypeProduitJPA.class))).thenReturn(null);
        
        final TypeProduit metier = fabriquerTypeProduit(CAMPING, ID_1);
        
        assertThatThrownBy(() -> this.service.update(metier))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.typeProduitDaoJPA).save(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>update(KO DAO) : wrappe en {@link ExceptionTechniqueGateway}.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("update(KO DAO) : jette ExceptionTechniqueGateway (wrap)")
    @Test
    public void testUpdateExceptionDAO() {
    	
        final TypeProduitJPA persistee = fabriquerTypeProduitJPA(VETEMENT, ID_1);
        
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(persistee));
        when(this.typeProduitDaoJPA.save(any(TypeProduitJPA.class))).thenThrow(new RuntimeException(MSG_BOOM));
        
        final TypeProduit metier = fabriquerTypeProduit(CAMPING, ID_1);
        
        assertThatThrownBy(() -> this.service.update(metier))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.typeProduitDaoJPA).save(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    // ============================= delete ===============================
    
    

    /**
     * <div>
     * <p>delete(null) : jette {@link ExceptionAppliParamNull}.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(null) : jette ExceptionAppliParamNull")
    @Test
    public void testDeleteNull() {
    	
        assertThatThrownBy(() -> this.service.delete(null))
            .isInstanceOf(ExceptionAppliParamNull.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_DELETE_KO_PARAM_NULL);
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>delete(ID null) : jette {@link ExceptionAppliParamNonPersistent}
     * avec {@link TypeProduitGatewayIService#MESSAGE_DELETE_KO_ID_NULL}.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(ID null) : jette ExceptionAppliParamNonPersistent")
    @Test
    public void testDeleteIdNull() {
    	
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, null);
        
        assertThatThrownBy(() -> this.service.delete(metier))
            .isInstanceOf(ExceptionAppliParamNonPersistent.class)
            .hasMessage(TypeProduitGatewayIService.MESSAGE_DELETE_KO_ID_NULL);
        verifyNoInteractions(this.typeProduitDaoJPA);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>delete(findById retourne null) : jette {@link ExceptionTechniqueGateway}
     * KO_STOCKAGE, et ne supprime pas.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(findById retourne null) : jette ExceptionTechniqueGateway KO_STOCKAGE")
    @Test
    public void testDeleteFindByIdDAORetourneNull() {
    	
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(null);
        
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);
        
        assertThatThrownBy(() -> this.service.delete(metier))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessage(MSG_ERREUR_TECH_KO_STOCKAGE);
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.typeProduitDaoJPA, never()).delete(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>delete(KO DAO sur findById) : wrappe en {@link ExceptionTechniqueGateway}.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(KO DAO sur findById) : jette ExceptionTechniqueGateway (wrap)")
    @Test
    public void testDeleteExceptionDAOFindById() {
    	
        when(this.typeProduitDaoJPA.findById(ID_1))
            .thenThrow(new RuntimeException(MSG_BOOM));
        
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);
        
        assertThatThrownBy(() -> this.service.delete(metier))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.typeProduitDaoJPA, never()).delete(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>delete(entity inexistante) : ne fait rien, ne supprime pas.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(entity inexistante) : ne fait rien")
    @Test
    public void testDeleteEntityInexistante() throws Exception {
    	
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.empty());
        
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);
        
        this.service.delete(metier);
        
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.typeProduitDaoJPA, never()).delete(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>delete(ID inexistant) : ne fait rien et ne lève pas d'exception.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(ID inexistant) : ne fait rien et ne lève pas d'exception")
    @Test
    public void testDeleteIdInexistant() throws Exception {
    	
        when(this.typeProduitDaoJPA.findById(ID_INEXISTANT)).thenReturn(Optional.empty());
        
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_INEXISTANT);
        
        this.service.delete(metier); // Pas d'exception attendue
        
        verify(this.typeProduitDaoJPA).findById(ID_INEXISTANT);
        verify(this.typeProduitDaoJPA, never()).delete(any(TypeProduitJPA.class));
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>delete(OK) : délègue delete() au DAO avec l'entity persistée.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(OK) : délègue delete() au DAO")
    @Test
    public void testDeleteOK() throws Exception {
    	
        final TypeProduitJPA persistee = fabriquerTypeProduitJPA(VETEMENT, ID_1);
        
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(persistee));
        
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);
        this.service.delete(metier);
        
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.typeProduitDaoJPA).delete(persistee);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>delete(KO DAO) : wrappe en {@link ExceptionTechniqueGateway}.</p>
     * </div>
     */
    @Tag(TAG_DELETE)
    @DisplayName("delete(KO DAO) : jette ExceptionTechniqueGateway (wrap)")
    @Test
    public void testDeleteExceptionDAO() {
    	
        final TypeProduitJPA persistee = fabriquerTypeProduitJPA(VETEMENT, ID_1);
        
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(persistee));
        
        org.mockito.Mockito.doThrow(new RuntimeException(MSG_BOOM))
            .when(this.typeProduitDaoJPA).delete(any(TypeProduitJPA.class));
        
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);
        
        assertThatThrownBy(() -> this.service.delete(metier))
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);
        verify(this.typeProduitDaoJPA).findById(ID_1);
        verify(this.typeProduitDaoJPA).delete(persistee);
        
    } // __________________________________________________________________
    
    

    // ============================== Count ===============================
    
    

    /**
     * <div>
     * <p>count() : délègue count() au DAO et retourne sa valeur.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_COUNT)
    @DisplayName("count() : délègue count() au DAO et retourne sa valeur")
    @Test
    public void testCount() throws Exception {
    	
        when(this.typeProduitDaoJPA.count()).thenReturn(TOTAL_10);
        
        final long resultat = this.service.count();
        
        assertThat(resultat).isEqualTo(TOTAL_10);
        verify(this.typeProduitDaoJPA).count();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>count() : retourne 0 si le stockage est vide.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_COUNT)
    @DisplayName("count() : retourne 0 si le stockage est vide")
    @Test
    public void testCountZero() throws Exception {
    	
        when(this.typeProduitDaoJPA.count()).thenReturn(TOTAL_0);
        
        final long resultat = this.service.count();
        
        assertThat(resultat).isEqualTo(TOTAL_0);
        verify(this.typeProduitDaoJPA).count();
        
    } // _________________________________________________________________
    
    

    /**
     * <div>
     * <p>count(KO DAO) : wrappe en {@link ExceptionTechniqueGateway}.</p>
     * </div>
     */
    @Tag(TAG_COUNT)
    @DisplayName("count(KO DAO) : jette ExceptionTechniqueGateway (wrap)")
    @Test
    public void testCountExceptionDAO() {
    	
        when(this.typeProduitDaoJPA.count()).thenThrow(new RuntimeException(MSG_BOOM));
        
        assertThatThrownBy(() -> this.service.count())
            .isInstanceOf(ExceptionTechniqueGateway.class)
            .hasMessageContaining(MSG_PREFIX_ERREUR_TECH)
            .hasMessageContaining(MSG_BOOM);
        verify(this.typeProduitDaoJPA).count();
        
    } // _________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>count(valeur négative) :
     * retourne la valeur telle quelle.</p>
     * </div>
     * @throws Exception
     */
    @Tag(TAG_COUNT)
    @DisplayName("count(valeur négative) : retourne la valeur telle quelle")
    @Test
    public void testCountValeurNegative() throws Exception {

        when(this.typeProduitDaoJPA.count()).thenReturn(-1L);

        final long resultat = this.service.count();

        assertThat(resultat).isEqualTo(-1L);
        verify(this.typeProduitDaoJPA).count();
        
    } // _________________________________________________________________
    


    // ============================== TRIS ================================
    
    

    /**
     * <div>
     * <p>convertirRequetePageEnPageable(null) : utilise une requête par défaut.</p>
     * </div>
     */
    @Tag(TAG_TRIS)
    @DisplayName("convertirRequetePageEnPageable(null) : utilise une requête par défaut")
    @Test
    public void testConvertirRequetePageEnPageableNull() throws Exception {
    	
        @SuppressWarnings(UNCHECKED)
        final Pageable pageable = (Pageable) invokePrivateMethod(
                this.service,
                CONV_REQ_PAGE,
                new Class<?>[]{RequetePage.class},
                new Object[]{null});
        
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(RequetePage.PAGE_DEFAUT);
        assertThat(pageable.getPageSize()).isEqualTo(RequetePage.TAILLE_DEFAUT);
        assertThat(pageable.getSort().isUnsorted()).isTrue();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>convertirRequetePageEnPageable(tris null) : retourne un Pageable non trié.</p>
     * </div>
     */
    @Tag(TAG_TRIS)
    @DisplayName("convertirRequetePageEnPageable(tris null) : retourne un Pageable non trié")
    @Test
    public void testConvertirRequetePageEnPageableTrisNull() throws Exception {
    	
        final RequetePage requete = new RequetePage(1, 5, null);
        
        @SuppressWarnings(UNCHECKED)
        final Pageable pageable = (Pageable) invokePrivateMethod(
                this.service,
                CONV_REQ_PAGE,
                new Class<?>[]{RequetePage.class},
                new Object[]{requete});
        
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().isUnsorted()).isTrue();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>convertirRequetePageEnPageable(tris vides) : retourne un Pageable non trié.</p>
     * </div>
     */
    @Tag(TAG_TRIS)
    @DisplayName("convertirRequetePageEnPageable(tris vides) : retourne un Pageable non trié")
    @Test
    public void testConvertirRequetePageEnPageableTrisVides() throws Exception {
    	
        final RequetePage requete = new RequetePage(1, 5, Collections.emptyList());
        
        @SuppressWarnings(UNCHECKED)
        final Pageable pageable = (Pageable) invokePrivateMethod(
                this.service,
                CONV_REQ_PAGE,
                new Class<?>[]{RequetePage.class},
                new Object[]{requete});
        
        assertThat(pageable).isNotNull();
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().isUnsorted()).isTrue();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>convertirRequetePageEnPageable(tris valides) : convertit en Sort Spring.</p>
     * </div>
     */
    @Tag(TAG_TRIS)
    @DisplayName("convertirRequetePageEnPageable(tris valides) : convertit en Sort Spring")
    @Test
    public void testConvertirRequetePageEnPageableTrisValides() throws Exception {
    	
        final List<TriSpec> tris = new ArrayList<TriSpec>();
        
        tris.add(new TriSpec(PROP_TYPEPRODUIT, DirectionTri.ASC));
        tris.add(new TriSpec(PROP_IDTYPEPRODUIT, DirectionTri.DESC));
        
        final RequetePage requete = new RequetePage(1, 5, tris);
        
        @SuppressWarnings(UNCHECKED)
        final Pageable pageable = (Pageable) invokePrivateMethod(
                this.service,
                CONV_REQ_PAGE,
                new Class<?>[]{RequetePage.class},
                new Object[]{requete});
        
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
     * <p>convertirRequetePageEnPageable(tris invalides) : ignore les tris invalides.</p>
     * </div>
     */
    @Tag(TAG_TRIS)
    @DisplayName("convertirRequetePageEnPageable(tris invalides) : ignore les tris invalides")
    @Test
    public void testConvertirRequetePageEnPageableTrisInvalides() throws Exception {
    	
        final List<TriSpec> tris = new ArrayList<TriSpec>();
        
        tris.add(null);
        tris.add(new TriSpec(BLANK, DirectionTri.ASC));
        tris.add(new TriSpec(PROP_TYPEPRODUIT, DirectionTri.ASC));
        
        final RequetePage requete = new RequetePage(1, 5, tris);
        
        @SuppressWarnings(UNCHECKED)
        final Pageable pageable = (Pageable) invokePrivateMethod(
                this.service,
                CONV_REQ_PAGE,
                new Class<?>[]{RequetePage.class},
                new Object[]{requete});
        
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
     * <p>filtrerTrierDedoublonner(null) : retourne une liste vide.</p>
     * </div>
     */
    @Tag(TAG_DEDOUBLONNAGE)
    @DisplayName("filtrerTrierDedoublonner(null) : retourne une liste vide")
    @Test
    public void testFiltrerTrierDedoublonnerNull() throws Exception {
    	
        @SuppressWarnings(UNCHECKED)
        final List<TypeProduit> resultat = (List<TypeProduit>) invokePrivateMethod(
                this.service,
                FILTRERTRIER,
                new Class<?>[]{List.class},
                new Object[]{null});
        
        assertThat(resultat).isNotNull().isEmpty();
        
    } // __________________________________________________________________
    
    
    
    /**
     * <div>
     * <p>filtrerTrierDedoublonner(vide) : retourne une liste vide.</p>
     * </div>
     */
    @Tag(TAG_DEDOUBLONNAGE)
    @DisplayName("filtrerTrierDedoublonner(vide) : retourne une liste vide")
    @Test
    public void testFiltrerTrierDedoublonnerVide() throws Exception {
    	
        @SuppressWarnings(UNCHECKED)
        final List<TypeProduit> resultat = (List<TypeProduit>) invokePrivateMethod(
                this.service,
                FILTRERTRIER,
                new Class<?>[]{List.class},
                new Object[]{Collections.emptyList()});
        
        assertThat(resultat).isNotNull().isEmpty();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>filtrerTrierDedoublonner(avec nulls) : filtre les nulls.</p>
     * </div>
     */
    @Tag(TAG_DEDOUBLONNAGE)
    @DisplayName("filtrerTrierDedoublonner(avec nulls) : filtre les nulls")
    @Test
    public void testFiltrerTrierDedoublonnerAvecNulls() throws Exception {
    	
        final List<TypeProduitJPA> entities = Arrays.asList(
            null,
            fabriquerTypeProduitJPA(VETEMENT, ID_1),
            null,
            fabriquerTypeProduitJPA(OUTILLAGE, ID_2));

        @SuppressWarnings(UNCHECKED)
        final List<TypeProduit> resultat = (List<TypeProduit>) invokePrivateMethod(
                this.service,
                FILTRERTRIER,
                new Class<?>[]{List.class},
                new Object[]{entities});

        assertThat(resultat).isNotNull().hasSize(2);
        assertThat(resultat)
            .extracting(TypeProduit::getTypeProduit)
            .containsExactly(OUTILLAGE, VETEMENT);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>filtrerTrierDedoublonner(case-insensitive) : dédoublonne sans tenir compte de la casse.</p>
     * </div>
     */
    @Tag(TAG_DEDOUBLONNAGE)
    @DisplayName("filtrerTrierDedoublonner(case-insensitive) : dédoublonne sans tenir compte de la casse")
    @Test
    public void testFiltrerTrierDedoublonnerCaseInsensitive() throws Exception {
    	
        final List<TypeProduitJPA> entities = Arrays.asList(
            fabriquerTypeProduitJPA("VÊTEMENT", ID_1),
            fabriquerTypeProduitJPA("vêtement", ID_2), // Doublon (case-insensitive)
            fabriquerTypeProduitJPA("OUTILLAGE", ID_3));

        @SuppressWarnings(UNCHECKED)
        final List<TypeProduit> resultat = (List<TypeProduit>) invokePrivateMethod(
                this.service,
                FILTRERTRIER,
                new Class<?>[]{List.class},
                new Object[]{entities});

        assertThat(resultat).hasSize(2); // Vêtement + Outillage (dédoublonnage appliqué)
        assertThat(resultat)
            .extracting(TypeProduit::getTypeProduit)
            .containsExactlyInAnyOrder("VÊTEMENT", "OUTILLAGE");
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>filtrerTrierDedoublonner(tri) : trie par libellé (case-insensitive).</p>
     * </div>
     */
    @Tag(TAG_DEDOUBLONNAGE)
    @DisplayName("filtrerTrierDedoublonner(tri) : trie par libellé (case-insensitive)")
    @Test
    public void testFiltrerTrierDedoublonnerTri() throws Exception {
    	
        final List<TypeProduitJPA> entities = Arrays.asList(
            fabriquerTypeProduitJPA(OUTILLAGE, ID_2),
            fabriquerTypeProduitJPA(VETEMENT, ID_1),
            fabriquerTypeProduitJPA(CAMPING, ID_3));

        @SuppressWarnings(UNCHECKED)
        final List<TypeProduit> resultat = (List<TypeProduit>) invokePrivateMethod(
                this.service,
                FILTRERTRIER,
                new Class<?>[]{List.class},
                new Object[]{entities});

        assertThat(resultat).hasSize(3);
        assertThat(resultat)
            .extracting(TypeProduit::getTypeProduit)
            .containsExactly(CAMPING, OUTILLAGE, VETEMENT); // Tri alphabétique
        
    } // __________________________________________________________________


    
    /**
     * <div>
     * <p>Test de la méthode privée appliquerModifications() avec null :
     * retourne false si un paramètre est null.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("appliquerModifications() : retourne false si un paramètre est null")
    @Test
    public void testAppliquerModificationsAvecNull() throws Exception {
    	
        final TypeProduitJPA entity = fabriquerTypeProduitJPA(VETEMENT, ID_1);
        final TypeProduit metier = fabriquerTypeProduit(CAMPING, ID_1);

        final boolean resultatNullPersistant = (boolean) invokePrivateMethod(
                this.service,
                APPLIQUER_MODIFS,
                new Class<?>[]{TypeProduitJPA.class, TypeProduit.class},
                new Object[]{null, metier});

        final boolean resultatNullMetier = (boolean) invokePrivateMethod(
                this.service,
                APPLIQUER_MODIFS,
                new Class<?>[]{TypeProduitJPA.class, TypeProduit.class},
                new Object[]{entity, null});

        assertThat(resultatNullPersistant).isFalse();
        assertThat(resultatNullMetier).isFalse();
        
    } // __________________________________________________________________

    
    
    /**
     * <div>
     * <p>Test de la méthode privée appliquerModifications() sans modification :
     * retourne false si les libellés sont identiques.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("appliquerModifications() : retourne false si les libellés sont identiques")
    @Test
    public void testAppliquerModificationsSansModification() throws Exception {
    	
        final TypeProduitJPA entity = fabriquerTypeProduitJPA(VETEMENT, ID_1);
        final TypeProduit metier = fabriquerTypeProduit(VETEMENT, ID_1);

        final boolean resultat = (boolean) invokePrivateMethod(
                this.service,
                APPLIQUER_MODIFS,
                new Class<?>[]{TypeProduitJPA.class, TypeProduit.class},
                new Object[]{entity, metier});

        assertThat(resultat).isFalse();
        assertThat(entity.getTypeProduit()).isEqualTo(VETEMENT);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Test de la méthode privée appliquerModifications() avec modification :
     * retourne true et modifie l'entité si les libellés sont différents.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("appliquerModifications() : retourne true et modifie l'entité si les libellés sont différents")
    @Test
    public void testAppliquerModificationsAvecModification() throws Exception {
    	
        final TypeProduitJPA entity = fabriquerTypeProduitJPA(VETEMENT, ID_1);
        final TypeProduit metier = fabriquerTypeProduit(CAMPING, ID_1);

        final boolean resultat = (boolean) invokePrivateMethod(
                this.service,
                APPLIQUER_MODIFS,
                new Class<?>[]{TypeProduitJPA.class, TypeProduit.class},
                new Object[]{entity, metier});

        assertThat(resultat).isTrue();
        assertThat(entity.getTypeProduit()).isEqualTo(CAMPING);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Test de la méthode privée safeEquals() avec null :
     * gère correctement les paramètres null.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("safeEquals() : gère correctement les paramètres null")
    @Test
    public void testSafeEqualsAvecNull() throws Exception {
    	
        final boolean resultat1 = (boolean) invokePrivateMethod(
                this.service,
                SAFE_EQUALS,
                new Class<?>[]{Object.class, Object.class},
                new Object[]{null, null});

        final boolean resultat2 = (boolean) invokePrivateMethod(
                this.service,
                SAFE_EQUALS,
                new Class<?>[]{Object.class, Object.class},
                new Object[]{VETEMENT, null});

        final boolean resultat3 = (boolean) invokePrivateMethod(
                this.service,
                SAFE_EQUALS,
                new Class<?>[]{Object.class, Object.class},
                new Object[]{null, VETEMENT});

        assertThat(resultat1).isTrue();
        assertThat(resultat2).isFalse();
        assertThat(resultat3).isFalse();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Test de la méthode privée safeEquals() avec objets égaux :
     * retourne true si les objets sont égaux.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("safeEquals() : retourne true si les objets sont égaux")
    @Test
    public void testSafeEqualsAvecObjetsEgaux() throws Exception {
    	
        final boolean resultat = (boolean) invokePrivateMethod(
                this.service,
                SAFE_EQUALS,
                new Class<?>[]{Object.class, Object.class},
                new Object[]{VETEMENT, VETEMENT});

        assertThat(resultat).isTrue();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Test de la méthode privée safeEquals() avec objets différents :
     * retourne false si les objets sont différents.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("safeEquals() : retourne false si les objets sont différents")
    @Test
    public void testSafeEqualsAvecObjetsDifferents() throws Exception {
    	
        final boolean resultat = (boolean) invokePrivateMethod(
                this.service,
                SAFE_EQUALS,
                new Class<?>[]{Object.class, Object.class},
                new Object[]{VETEMENT, OUTILLAGE});

        assertThat(resultat).isFalse();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Test de la méthode privée isBlank() avec null :
     * retourne true si le paramètre est null.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("isBlank() : retourne true si le paramètre est null")
    @Test
    public void testIsBlankAvecNull() throws Exception {
    	
        final boolean resultat = (boolean) invokePrivateMethod(
                this.service,
                ISBLANK,
                new Class<?>[]{String.class},
                new Object[]{null});

        assertThat(resultat).isTrue();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Test de la méthode privée isBlank() avec chaîne vide :
     * retourne true si le paramètre est vide.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("isBlank() : retourne true si le paramètre est vide")
    @Test
    public void testIsBlankAvecChaineVide() throws Exception {
    	
        final boolean resultat = (boolean) invokePrivateMethod(
                this.service,
                ISBLANK,
                new Class<?>[]{String.class},
                new Object[]{""});

        assertThat(resultat).isTrue();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Test de la méthode privée isBlank() avec chaîne blanche :
     * retourne true si le paramètre est blanc.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("isBlank() : retourne true si le paramètre est blanc")
    @Test
    public void testIsBlankAvecChaineBlanche() throws Exception {
    	
        final boolean resultat = (boolean) invokePrivateMethod(
                this.service,
                ISBLANK,
                new Class<?>[]{String.class},
                new Object[]{BLANK});

        assertThat(resultat).isTrue();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Test de la méthode privée isBlank() avec chaîne valide :
     * retourne false si le paramètre est valide.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("isBlank() : retourne false si le paramètre est valide")
    @Test
    public void testIsBlankAvecChaineValide() throws Exception {
    	
        final boolean resultat = (boolean) invokePrivateMethod(
                this.service,
                ISBLANK,
                new Class<?>[]{String.class},
                new Object[]{VETEMENT});

        assertThat(resultat).isFalse();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Test de la méthode privée safeMessage() avec null :
     * retourne une chaîne vide si le paramètre est null.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("safeMessage() : retourne une chaîne vide si le paramètre est null")
    @Test
    public void testSafeMessageAvecNull() throws Exception {
    	
        final String resultat = (String) invokePrivateMethod(
                this.service,
                "safeMessage",
                new Class<?>[]{Object.class},
                new Object[]{null});

        assertThat(resultat).isEmpty();
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Test de la méthode privée safeMessage() avec objet valide :
     * retourne la représentation textuelle de l'objet.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("safeMessage() : retourne la représentation textuelle de l'objet")
    @Test
    public void testSafeMessageAvecObjetValide() throws Exception {
    	
        final String resultat = (String) invokePrivateMethod(
                this.service,
                "safeMessage",
                new Class<?>[]{Object.class},
                new Object[]{VETEMENT});

        assertThat(resultat).isEqualTo(VETEMENT);
        
    } // __________________________________________________________________
    
    

    /**
     * <div>
     * <p>Test de la méthode privée safeMessage() avec objet dont toString() retourne null :
     * retourne une chaîne vide si toString() retourne null.</p>
     * </div>
     */
    @Tag(TAG_UPDATE)
    @DisplayName("safeMessage() : retourne une chaîne vide si toString() retourne null")
    @Test
    public void testSafeMessageAvecToStringNull() throws Exception {
    	
        final Object objet = new Object() {
            @Override
            public String toString() {
                return null;
            }
        };

        final String resultat = (String) invokePrivateMethod(
                this.service,
                "safeMessage",
                new Class<?>[]{Object.class},
                new Object[]{objet});

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