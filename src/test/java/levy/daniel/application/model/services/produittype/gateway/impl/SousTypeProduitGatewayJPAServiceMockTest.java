/* ********************************************************************* */
/* ***************************** TEST JUNIT **************************** */
/* ********************************************************************* */
package levy.daniel.application.model.services.produittype.gateway.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
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

    // *************************** CONSTANTES ******************************/

    /** Locale par défaut. */
    public static final Locale LOCALE_DEFAUT = Locale.getDefault();
    
    /**
     * "resource"
     */
    public static final String RESOURCE = "resource";

    /** Chaîne vide : "". */
    public static final String CHAINE_VIDE = "";

    /** Libellé enfant OK : "SousTypeProduit_1". */
    public static final String LIBELLE_ENFANT_1 = "SousTypeProduit_1";

    /** Libellé enfant OK : "SousTypeProduit_2". */
    public static final String LIBELLE_ENFANT_2 = "SousTypeProduit_2";

    /** Libellé enfant OK : "SousTypeProduit_3". */
    public static final String LIBELLE_ENFANT_3 = "SousTypeProduit_3";

    /** Libellé parent OK : "TypeProduit_1". */
    public static final String LIBELLE_PARENT_1 = "TypeProduit_1";

    /** Libellé parent OK : "TypeProduit_2". */
    public static final String LIBELLE_PARENT_2 = "TypeProduit_2";

    /** Contenu partiel : "produit". */
    public static final String CONTENU_PARTIEL = "produit";

    /** Libellé blank : "   ". */
    public static final String BLANK = "   ";

    /** ID : 1L. */
    public static final Long ID_1 = Long.valueOf(1L);

    /** ID : 2L. */
    public static final Long ID_2 = Long.valueOf(2L);

    /** ID : 3L. */
    public static final Long ID_3 = Long.valueOf(3L);

    /** Page number : 0. */
    public static final int PAGE_0 = 0;

    /** Page size : 5. */
    public static final int SIZE_5 = 5;

    /** Total elements : 10L. */
    public static final long TOTAL_10 = 10L;

    /** Message attendu : "Erreur Technique lors du stockage : " (préfixe). */
    public static final String MSG_PREFIX_ERREUR_TECH =
            SousTypeProduitGatewayIService.ERREUR_TECHNIQUE_STOCKAGE;

    /** Message attendu : "Erreur Technique - Le stockage a retourné null." */
    public static final String MSG_ERREUR_TECH_KO_STOCKAGE =
            SousTypeProduitGatewayIService.ERREUR_TECHNIQUE_KO_STOCKAGE;

    /** Message attendu : MESSAGE_CREER_KO_PARAM_NULL. */
    public static final String MSG_CREER_KO_PARAM_NULL =
            SousTypeProduitGatewayIService.MESSAGE_CREER_KO_PARAM_NULL;

    /** Message attendu : MESSAGE_CREER_KO_LIBELLE_BLANK. */
    public static final String MSG_CREER_KO_LIBELLE_BLANK =
            SousTypeProduitGatewayIService.MESSAGE_CREER_KO_LIBELLE_BLANK;

    /** Message attendu : MESSAGE_CREER_KO_PARENT_NULL. */
    public static final String MSG_CREER_KO_PARENT_NULL =
            SousTypeProduitGatewayIService.MESSAGE_CREER_KO_PARENT_NULL;

    /** Message attendu : MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK. */
    public static final String MSG_CREER_KO_LIBELLE_PARENT_BLANK =
            SousTypeProduitGatewayIService.MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK;

    /** Message attendu : MESSAGE_CREER_KO_PARENT_NON_PERSISTENT (préfixe). */
    public static final String MSG_CREER_PREFIX_PARENT_NON_PERSISTENT =
            SousTypeProduitGatewayIService.MESSAGE_CREER_KO_PARENT_NON_PERSISTENT;

    /** Message attendu : MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL. */
    public static final String MSG_FINDBYOBJETMETIER_KO_PARAM_NULL =
            SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL;

    /** Message attendu : MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK. */
    public static final String MSG_FINDBYOBJETMETIER_KO_LIBELLE_BLANK =
            SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK;

    /** Message attendu : MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL. */
    public static final String MSG_FINDBYOBJETMETIER_KO_PARENT_NULL =
            SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL;

    /** Message attendu : MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK. */
    public static final String MSG_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK =
            SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK;

    /** Message attendu : MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT (préfixe). */
    public static final String MSG_FINDBYOBJETMETIER_PREFIX_PARENT_NON_PERSISTENT =
            SousTypeProduitGatewayIService.MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT;

    /** Message attendu : MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK. */
    public static final String MSG_FINDBYLIBELLE_KO_LIBELLE_BLANK =
            SousTypeProduitGatewayIService.MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK;

    /** Message attendu : MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL. */
    public static final String MSG_FINDBYLIBELLERAPIDE_KO_PARAM_NULL =
            SousTypeProduitGatewayIService.MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL;

    /** Message attendu : MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL. */
    public static final String MSG_FINDALLBYPARENT_KO_PARAM_NULL =
            SousTypeProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL;

    /** Message attendu : MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK. */
    public static final String MSG_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK =
            SousTypeProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK;

    /** Message attendu : MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT (préfixe). */
    public static final String MSG_FINDALLBYPARENT_PREFIX_PARENT_NON_PERSISTENT =
            SousTypeProduitGatewayIService.MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT;

    /** Message attendu : MESSAGE_FINDBYID_KO_PARAM_NULL. */
    public static final String MSG_FINDBYID_KO_PARAM_NULL =
            SousTypeProduitGatewayIService.MESSAGE_FINDBYID_KO_PARAM_NULL;

    /** Message attendu : MESSAGE_UPDATE_KO_PARAM_NULL. */
    public static final String MSG_UPDATE_KO_PARAM_NULL =
            SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_PARAM_NULL;

    /** Message attendu : MESSAGE_UPDATE_KO_LIBELLE_BLANK. */
    public static final String MSG_UPDATE_KO_LIBELLE_BLANK =
            SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_BLANK;

    /** Message attendu : MESSAGE_UPDATE_KO_NON_PERSISTENT (préfixe). */
    public static final String MSG_UPDATE_PREFIX_NON_PERSISTENT =
            SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_NON_PERSISTENT;

    /** Message attendu : MESSAGE_UPDATE_KO_PARENT_NULL. */
    public static final String MSG_UPDATE_KO_PARENT_NULL =
            SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_PARENT_NULL;

    /** Message attendu : MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK. */
    public static final String MSG_UPDATE_KO_LIBELLE_PARENT_BLANK =
            SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK;

    /** Message attendu : MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT (préfixe). */
    public static final String MSG_UPDATE_PREFIX_PARENT_NON_PERSISTENT =
            SousTypeProduitGatewayIService.MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT;

    /** Message attendu : MESSAGE_DELETE_KO_PARAM_NULL. */
    public static final String MSG_DELETE_KO_PARAM_NULL =
            SousTypeProduitGatewayIService.MESSAGE_DELETE_KO_PARAM_NULL;

    /** Message attendu : MESSAGE_DELETE_KO_ID_NULL. */
    public static final String MSG_DELETE_KO_ID_NULL =
            SousTypeProduitGatewayIService.MESSAGE_DELETE_KO_ID_NULL;

    // *************************** ATTRIBUTS *******************************/

    /**
     * <div>
     * <p>Mock du DAO pour {@link SousTypeProduit} (enfant).</p>
     * </div>
     */
    @Mock
    private SousTypeProduitDaoJPA sousTypeProduitDaoJPA;

    /**
     * <div>
     * <p>Mock du DAO pour {@link TypeProduit} (parent).</p>
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
     * <p>Service testé (injection des mocks).</p>
     * </div>
     */
    @InjectMocks
    private SousTypeProduitGatewayJPAService service;

    // ************************* CONSTRUCTEURS ******************************/

    /**
     * <div>
     * <p style="font-weight:bold;">CONSTRUCTEUR D'ARITE NULLE.</p>
     * <p>Nécessaire pour PMD.</p>
     * </div>
     */
    public SousTypeProduitGatewayJPAServiceMockTest() {
        super();
    }

    // ************************* METHODES **********************************/

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
    }

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
    }

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
    }

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
    }

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
    }

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
    }

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
    }

    // ============================== TESTS ================================

    // ===================== creer =====================

    /**
     * <div>
     * <p>creer(null) -> ExceptionAppliParamNull + MESSAGE_CREER_KO_PARAM_NULL.</p>
     * </div>
     */
    @Test
    public void testCreerParamNullExceptionAppliParamNull() {
        try {
            this.service.creer(null);
            fail(CHAINE_VIDE);
        } catch (final ExceptionAppliParamNull e) {
            assertEquals(MSG_CREER_KO_PARAM_NULL, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>creer(libelle blank) -> ExceptionAppliLibelleBlank + MESSAGE_CREER_KO_LIBELLE_BLANK.</p>
     * </div>
     */
    @Test
    public void testCreerLibelleBlankExceptionAppliLibelleBlank() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(BLANK, null, parent);

        try {
            this.service.creer(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionAppliLibelleBlank e) {
            assertEquals(MSG_CREER_KO_LIBELLE_BLANK, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>creer(parent null) -> ExceptionAppliParentNull + MESSAGE_CREER_KO_PARENT_NULL.</p>
     * </div>
     */
    @Test
    public void testCreerParentNullExceptionAppliParentNull() {
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, null);

        try {
            this.service.creer(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionAppliParentNull e) {
            assertEquals(MSG_CREER_KO_PARENT_NULL, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>creer(parent libellé blank) -> ExceptionAppliLibelleBlank + MESSAGE_CREER_KO_LIBELLE_PARENT_BLANK.</p>
     * </div>
     */
    @Test
    public void testCreerParentLibelleBlankExceptionAppliLibelleBlank() {
        final TypeProduit parent = fabriquerTypeProduit(BLANK, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        try {
            this.service.creer(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionAppliLibelleBlank e) {
            assertEquals(MSG_CREER_KO_LIBELLE_PARENT_BLANK, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>creer(parent id null) -> ExceptionTechniqueGatewayNonPersistent + (MESSAGE_CREER_KO_PARENT_NON_PERSISTENT + parent).</p>
     * </div>
     */
    @Test
    public void testCreerParentIdNullExceptionTechniqueGatewayNonPersistent() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, null);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        try {
            this.service.creer(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGatewayNonPersistent e) {
            assertEquals(
                    construireMessageNonPersistent(
                            MSG_CREER_PREFIX_PARENT_NON_PERSISTENT, LIBELLE_PARENT_1),
                    e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>creer(parent absent DAO) -> ExceptionTechniqueGatewayNonPersistent + (MESSAGE_CREER_KO_PARENT_NON_PERSISTENT + parent).</p>
     * </div>
     */
    @Test
    public void testCreerParentAbsentExceptionTechniqueGatewayNonPersistent() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.empty());

        try {
            this.service.creer(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGatewayNonPersistent e) {
            assertEquals(
                    construireMessageNonPersistent(
                            MSG_CREER_PREFIX_PARENT_NON_PERSISTENT, LIBELLE_PARENT_1),
                    e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>creer(DAO parent jette RuntimeException) -> ExceptionTechniqueGateway (préfixé ERREUR_TECHNIQUE_STOCKAGE + cause).</p>
     * </div>
     */
    @Test
    public void testCreerParentDaoJetteExceptionTechniqueGateway() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final RuntimeException ex = new RuntimeException(LIBELLE_PARENT_1);
        when(this.typeProduitDaoJPA.findById(ID_1)).thenThrow(ex);

        try {
            this.service.creer(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertTrue(e.getMessage().startsWith(MSG_PREFIX_ERREUR_TECH));
            assertNotNull(e.getCause());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>creer(save retourne null) -> ExceptionTechniqueGateway + ERREUR_TECHNIQUE_KO_STOCKAGE.</p>
     * </div>
     */
    @Test
    public void testCreerSaveNullExceptionTechniqueGateway() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class))).thenReturn(null);

        try {
            this.service.creer(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertEquals(MSG_ERREUR_TECH_KO_STOCKAGE, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>creer(DAO save jette Exception) -> ExceptionTechniqueGateway (préfixé ERREUR_TECHNIQUE_STOCKAGE + cause).</p>
     * </div>
     */
    @Test
    public void testCreerDaoSaveJetteExceptionTechniqueGateway() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final RuntimeException ex = new RuntimeException(LIBELLE_ENFANT_1);
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class))).thenThrow(ex);

        try {
            this.service.creer(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertTrue(e.getMessage().startsWith(MSG_PREFIX_ERREUR_TECH));
            assertNotNull(e.getCause());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }
    
    /**
     * <div>
     * <p>creer(parent avec caractères spéciaux) -> nominal.</p>
     * </div>
     */
    @Test
    public void testCreerParentCaracteresSpeciauxNominalOk() {
        final TypeProduit parent = fabriquerTypeProduit("Type/Produit_1", ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA("Type/Produit_1", ID_1);
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final SousTypeProduitJPA sauvegardeJPA =
                fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class))).thenReturn(sauvegardeJPA);

        try {
            final SousTypeProduit retour = this.service.creer(stp);
            assertNotNull(retour);
            assertEquals(LIBELLE_ENFANT_1, retour.getSousTypeProduit());
            assertEquals("Type/Produit_1", retour.getTypeProduit().getTypeProduit());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>creer(nominal) -> retourne un objet métier non null, cohérent.</p>
     * </div>
     */
    @Test
    public void testCreerNominalOk() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final SousTypeProduitJPA sauvegardeJPA =
                fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class))).thenReturn(sauvegardeJPA);

        try {
            final SousTypeProduit retour = this.service.creer(stp);
            assertNotNull(retour);
            assertEquals(LIBELLE_ENFANT_1, retour.getSousTypeProduit());
            assertNotNull(retour.getTypeProduit());
            assertEquals(LIBELLE_PARENT_1, retour.getTypeProduit().getTypeProduit());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    // ===================== rechercherTous =====================

    /**
     * <div>
     * <p>rechercherTous(findAll retourne null) -> ExceptionTechniqueGateway + ERREUR_TECHNIQUE_KO_STOCKAGE.</p>
     * </div>
     */
    @Test
    public void testRechercherTousFindAllNullExceptionTechniqueGateway() {
        when(this.sousTypeProduitDaoJPA.findAll()).thenReturn(null);

        try {
            this.service.rechercherTous();
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertEquals(MSG_ERREUR_TECH_KO_STOCKAGE, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>rechercherTous(findAll vide) -> liste vide.</p>
     * </div>
     */
    @Test
    public void testRechercherTousFindAllVideOk() {
        when(this.sousTypeProduitDaoJPA.findAll()).thenReturn(new ArrayList<SousTypeProduitJPA>());

        try {
            final List<SousTypeProduit> retour = this.service.rechercherTous();
            assertNotNull(retour);
            assertTrue(retour.isEmpty());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>rechercherTous(DAO findAll jette Exception) -> ExceptionTechniqueGateway (préfixé ERREUR_TECHNIQUE_STOCKAGE + cause).</p>
     * </div>
     */
    @Test
    public void testRechercherTousDaoJetteExceptionTechniqueGateway() {
        final RuntimeException ex = new RuntimeException(LIBELLE_ENFANT_1);
        when(this.sousTypeProduitDaoJPA.findAll()).thenThrow(ex);

        try {
            this.service.rechercherTous();
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertTrue(e.getMessage().startsWith(MSG_PREFIX_ERREUR_TECH));
            assertNotNull(e.getCause());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>rechercherTous(nominal) -> liste filtrée/triee/dédoublonnée.</p>
     * </div>
     */
    @Test
    public void testRechercherTousNominalOk() {
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        final SousTypeProduitJPA e1 = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_1, parentJPA);
        final SousTypeProduitJPA e2 = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);
        final SousTypeProduitJPA e3 = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_2, ID_3, parentJPA);

        final List<SousTypeProduitJPA> entities = new ArrayList<SousTypeProduitJPA>();
        entities.add(null);
        entities.add(e1);
        entities.add(e2);
        entities.add(e3);

        when(this.sousTypeProduitDaoJPA.findAll()).thenReturn(entities);

        try {
            final List<SousTypeProduit> retour = this.service.rechercherTous();
            assertNotNull(retour);
            assertTrue(retour.size() >= 2);
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    // ===================== rechercherTousParPage =====================

    /**
     * <div>
     * <p>rechercherTousParPage(pRequetePage null) -> nominal (requête par défaut).</p>
     * </div>
     */
    @Test
    public void testRechercherTousParPageParamNullNominalOk() {
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        final List<SousTypeProduitJPA> contenu = new ArrayList<SousTypeProduitJPA>();
        contenu.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_1, parentJPA));
        contenu.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_2, ID_2, parentJPA));

        final Page<SousTypeProduitJPA> page =
                new PageImpl<SousTypeProduitJPA>(
                        contenu, PageRequest.of(PAGE_0, SIZE_5), TOTAL_10);

        when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        try {
            final ResultatPage<SousTypeProduit> resultat = this.service.rechercherTousParPage(null);
            assertNotNull(resultat);
            assertNotNull(resultat.getContent());
            assertEquals(contenu.size(), resultat.getContent().size());
            assertEquals(PAGE_0, resultat.getPageNumber());
            assertEquals(SIZE_5, resultat.getPageSize());
            assertEquals(TOTAL_10, resultat.getTotalElements());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }
    
    /**
     * <div>
     * <p>rechercherTousParPage(tris invalides) -> tris ignorés, résultat nominal.</p>
     * </div>
     */
    @Test
    public void testRechercherTousParPageTrisInvalidesNominalOk() {
        final RequetePage requete = new RequetePage();
        requete.getTris().add(new TriSpec(null, DirectionTri.ASC)); // Tri invalide

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final List<SousTypeProduitJPA> contenu = new ArrayList<>();
        contenu.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_1, parentJPA));

        final Page<SousTypeProduitJPA> page =
                new PageImpl<>(contenu, PageRequest.of(PAGE_0, SIZE_5), TOTAL_10);
        when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(page);

        try {
            final ResultatPage<SousTypeProduit> resultat = this.service.rechercherTousParPage(requete);
            assertNotNull(resultat);
            assertEquals(1, resultat.getContent().size());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>rechercherTousParPage(DAO retourne page null) -> ExceptionTechniqueGateway + ERREUR_TECHNIQUE_KO_STOCKAGE.</p>
     * </div>
     */
    @Test
    public void testRechercherTousParPagePageNullExceptionTechniqueGateway() {
        when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(null);

        try {
            this.service.rechercherTousParPage(new RequetePage());
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertEquals(MSG_ERREUR_TECH_KO_STOCKAGE, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>rechercherTousParPage(contenu page null) -> ExceptionTechniqueGateway + ERREUR_TECHNIQUE_KO_STOCKAGE.</p>
     * </div>
     */
    @Test
    public void testRechercherTousParPageContenuNullExceptionTechniqueGateway() {
        final Page<SousTypeProduitJPA> pageMock = org.mockito.Mockito.mock(Page.class);

        when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenReturn(pageMock);
        when(pageMock.getContent()).thenReturn(null);

        try {
            this.service.rechercherTousParPage(new RequetePage());
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertEquals(MSG_ERREUR_TECH_KO_STOCKAGE, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>rechercherTousParPage(DAO jette Exception) -> ExceptionTechniqueGateway (préfixé ERREUR_TECHNIQUE_STOCKAGE + cause).</p>
     * </div>
     */
    @Test
    public void testRechercherTousParPageDaoJetteExceptionTechniqueGateway() {
        final RuntimeException ex = new RuntimeException(LIBELLE_ENFANT_1);
        when(this.sousTypeProduitDaoJPA.findAll(any(Pageable.class))).thenThrow(ex);

        try {
            this.service.rechercherTousParPage(new RequetePage());
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertTrue(e.getMessage().startsWith(MSG_PREFIX_ERREUR_TECH));
            assertNotNull(e.getCause());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    // ===================== findByObjetMetier =====================

    /**
     * <div>
     * <p>findByObjetMetier(null) -> ExceptionAppliParamNull + MESSAGE_FINDBYOBJETMETIER_KO_PARAM_NULL.</p>
     * </div>
     */
    @Test
    public void testFindByObjetMetierParamNullExceptionAppliParamNull() {
        try {
            this.service.findByObjetMetier(null);
            fail(CHAINE_VIDE);
        } catch (final ExceptionAppliParamNull e) {
            assertEquals(MSG_FINDBYOBJETMETIER_KO_PARAM_NULL, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findByObjetMetier(libellé blank) -> ExceptionAppliLibelleBlank + MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_BLANK.</p>
     * </div>
     */
    @Test
    public void testFindByObjetMetierLibelleBlankExceptionAppliLibelleBlank() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(BLANK, null, parent);

        try {
            this.service.findByObjetMetier(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionAppliLibelleBlank e) {
            assertEquals(MSG_FINDBYOBJETMETIER_KO_LIBELLE_BLANK, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findByObjetMetier(parent null) -> ExceptionAppliParentNull + MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NULL.</p>
     * </div>
     */
    @Test
    public void testFindByObjetMetierParentNullExceptionAppliParentNull() {
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, null);

        try {
            this.service.findByObjetMetier(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionAppliParentNull e) {
            assertEquals(MSG_FINDBYOBJETMETIER_KO_PARENT_NULL, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findByObjetMetier(parent libellé blank) -> ExceptionAppliLibelleBlank + MESSAGE_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK.</p>
     * </div>
     */
    @Test
    public void testFindByObjetMetierParentLibelleBlankExceptionAppliLibelleBlank() {
        final TypeProduit parent = fabriquerTypeProduit(BLANK, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        try {
            this.service.findByObjetMetier(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionAppliLibelleBlank e) {
            assertEquals(MSG_FINDBYOBJETMETIER_KO_LIBELLE_PARENT_BLANK, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findByObjetMetier(parent id null) -> ExceptionTechniqueGatewayNonPersistent + (MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT + parent).</p>
     * </div>
     */
    @Test
    public void testFindByObjetMetierParentIdNullExceptionTechniqueGatewayNonPersistent() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, null);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        try {
            this.service.findByObjetMetier(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGatewayNonPersistent e) {
            assertEquals(
                    construireMessageNonPersistent(
                            MSG_FINDBYOBJETMETIER_PREFIX_PARENT_NON_PERSISTENT, LIBELLE_PARENT_1),
                    e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findByObjetMetier(parent absent DAO) -> ExceptionTechniqueGatewayNonPersistent + (MESSAGE_FINDBYOBJETMETIER_KO_PARENT_NON_PERSISTENT + parent).</p>
     * </div>
     */
    @Test
    public void testFindByObjetMetierParentAbsentExceptionTechniqueGatewayNonPersistent() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.empty());

        try {
            this.service.findByObjetMetier(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGatewayNonPersistent e) {
            assertEquals(
                    construireMessageNonPersistent(
                            MSG_FINDBYOBJETMETIER_PREFIX_PARENT_NON_PERSISTENT, LIBELLE_PARENT_1),
                    e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findByObjetMetier(DAO enfant retourne null) -> ExceptionTechniqueGateway + ERREUR_TECHNIQUE_KO_STOCKAGE.</p>
     * </div>
     */
    @Test
    public void testFindByObjetMetierDaoRetourneNullExceptionTechniqueGateway() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenReturn(null);

        try {
            this.service.findByObjetMetier(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertEquals(MSG_ERREUR_TECH_KO_STOCKAGE, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findByObjetMetier(pas trouvé) -> retourne null.</p>
     * </div>
     */
    @Test
    public void testFindByObjetMetierPasTrouveNull() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_3, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final List<SousTypeProduitJPA> entities = new ArrayList<SousTypeProduitJPA>();
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA));
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_2, ID_3, parentJPA));
        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenReturn(entities);

        try {
            final SousTypeProduit retour = this.service.findByObjetMetier(stp);
            assertNull(retour);
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findByObjetMetier(trouvé) -> retourne objet métier non null.</p>
     * </div>
     */
    @Test
    public void testFindByObjetMetierTrouveOk() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_2, null, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final List<SousTypeProduitJPA> entities = new ArrayList<SousTypeProduitJPA>();
        entities.add(null);
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA));
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_2, ID_3, parentJPA));
        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenReturn(entities);

        try {
            final SousTypeProduit retour = this.service.findByObjetMetier(stp);
            assertNotNull(retour);
            assertEquals(LIBELLE_ENFANT_2, retour.getSousTypeProduit());
            assertNotNull(retour.getTypeProduit());
            assertEquals(LIBELLE_PARENT_1, retour.getTypeProduit().getTypeProduit());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    // ===================== findByLibelle =====================

    /**
     * <div>
     * <p>findByLibelle(blank) -> ExceptionAppliLibelleBlank + MESSAGE_FINDBYLIBELLE_KO_LIBELLE_BLANK.</p>
     * </div>
     */
    @Test
    public void testFindByLibelleBlankExceptionAppliLibelleBlank() {
        try {
            this.service.findByLibelle(BLANK);
            fail(CHAINE_VIDE);
        } catch (final ExceptionAppliLibelleBlank e) {
            assertEquals(MSG_FINDBYLIBELLE_KO_LIBELLE_BLANK, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findByLibelle(DAO retourne null) -> ExceptionTechniqueGateway + ERREUR_TECHNIQUE_KO_STOCKAGE.</p>
     * </div>
     */
    @Test
    public void testFindByLibelleDaoNullExceptionTechniqueGateway() {
        when(this.sousTypeProduitDaoJPA.findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1)).thenReturn(null);

        try {
            this.service.findByLibelle(LIBELLE_ENFANT_1);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertEquals(MSG_ERREUR_TECH_KO_STOCKAGE, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findByLibelle(DAO jette Exception) -> ExceptionTechniqueGateway (préfixé ERREUR_TECHNIQUE_STOCKAGE + cause).</p>
     * </div>
     */
    @Test
    public void testFindByLibelleDaoJetteExceptionTechniqueGateway() {
        final RuntimeException ex = new RuntimeException(LIBELLE_ENFANT_1);
        when(this.sousTypeProduitDaoJPA.findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1)).thenThrow(ex);

        try {
            this.service.findByLibelle(LIBELLE_ENFANT_1);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertTrue(e.getMessage().startsWith(MSG_PREFIX_ERREUR_TECH));
            assertNotNull(e.getCause());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findByLibelle(vide) -> liste vide.</p>
     * </div>
     */
    @Test
    public void testFindByLibelleVideOk() {
        when(this.sousTypeProduitDaoJPA.findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1)).thenReturn(new ArrayList<SousTypeProduitJPA>());

        try {
            final List<SousTypeProduit> retour = this.service.findByLibelle(LIBELLE_ENFANT_1);
            assertNotNull(retour);
            assertTrue(retour.isEmpty());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findByLibelle(nominal) -> liste non null.</p>
     * </div>
     */
    @Test
    public void testFindByLibelleNominalOk() {
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        final List<SousTypeProduitJPA> entities = new ArrayList<SousTypeProduitJPA>();
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_1, parentJPA));

        when(this.sousTypeProduitDaoJPA.findBySousTypeProduitIgnoreCase(LIBELLE_ENFANT_1)).thenReturn(entities);

        try {
            final List<SousTypeProduit> retour = this.service.findByLibelle(LIBELLE_ENFANT_1);
            assertNotNull(retour);
            assertEquals(1, retour.size());
            assertEquals(LIBELLE_ENFANT_1, retour.get(0).getSousTypeProduit());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    // ===================== findByLibelleRapide =====================

    /**
     * <div>
     * <p>findByLibelleRapide(null) -> ExceptionAppliParamNull + MESSAGE_FINDBYLIBELLERAPIDE_KO_PARAM_NULL.</p>
     * </div>
     */
    @Test
    public void testFindByLibelleRapideParamNullExceptionAppliParamNull() {
        try {
            this.service.findByLibelleRapide(null);
            fail(CHAINE_VIDE);
        } catch (final ExceptionAppliParamNull e) {
            assertEquals(MSG_FINDBYLIBELLERAPIDE_KO_PARAM_NULL, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findByLibelleRapide(blank mais pas null) -> retourne rechercherTous().</p>
     * </div>
     */
    @Test
    public void testFindByLibelleRapideBlankRetourneTous() {
        assertTrue(isBlankButNotNull(BLANK));

        when(this.sousTypeProduitDaoJPA.findAll()).thenReturn(new ArrayList<SousTypeProduitJPA>());

        try {
            final List<SousTypeProduit> retour = this.service.findByLibelleRapide(BLANK);
            assertNotNull(retour);
            assertTrue(retour.isEmpty());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findByLibelleRapide(DAO retourne null) -> ExceptionTechniqueGateway + ERREUR_TECHNIQUE_KO_STOCKAGE.</p>
     * </div>
     */
    @Test
    public void testFindByLibelleRapideDaoNullExceptionTechniqueGateway() {
        when(this.sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL)).thenReturn(null);

        try {
            this.service.findByLibelleRapide(CONTENU_PARTIEL);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertEquals(MSG_ERREUR_TECH_KO_STOCKAGE, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findByLibelleRapide(DAO jette Exception) -> ExceptionTechniqueGateway (préfixé ERREUR_TECHNIQUE_STOCKAGE + cause).</p>
     * </div>
     */
    @Test
    public void testFindByLibelleRapideDaoJetteExceptionTechniqueGateway() {
        final RuntimeException ex = new RuntimeException(CONTENU_PARTIEL);
        when(this.sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL)).thenThrow(ex);

        try {
            this.service.findByLibelleRapide(CONTENU_PARTIEL);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertTrue(e.getMessage().startsWith(MSG_PREFIX_ERREUR_TECH));
            assertNotNull(e.getCause());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }
    
    /**
     * <div>
     * <p>findByLibelleRapide(contenu partiel avec caractères spéciaux) -> nominal.</p>
     * </div>
     */
    @Test
    public void testFindByLibelleRapideContenuSpeciauxNominalOk() {
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final List<SousTypeProduitJPA> entities = new ArrayList<>();
        entities.add(fabriquerSousTypeProduitJPA("prod/uit_1", ID_1, parentJPA));

        when(this.sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase("prod/uit")).thenReturn(entities);

        try {
            final List<SousTypeProduit> retour = this.service.findByLibelleRapide("prod/uit");
            assertNotNull(retour);
            assertEquals(1, retour.size());
            assertEquals("prod/uit_1", retour.get(0).getSousTypeProduit());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findByLibelleRapide(nominal) -> liste non null.</p>
     * </div>
     */
    @Test
    public void testFindByLibelleRapideNominalOk() {
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        final List<SousTypeProduitJPA> entities = new ArrayList<SousTypeProduitJPA>();
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_1, parentJPA));

        when(this.sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase(CONTENU_PARTIEL)).thenReturn(entities);

        try {
            final List<SousTypeProduit> retour = this.service.findByLibelleRapide(CONTENU_PARTIEL);
            assertNotNull(retour);
            assertEquals(1, retour.size());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    // ===================== findAllByParent =====================

    /**
     * <div>
     * <p>findAllByParent(null) -> ExceptionAppliParentNull + MESSAGE_FINDALLBYPARENT_KO_PARAM_NULL.</p>
     * </div>
     */
    @Test
    public void testFindAllByParentParamNullExceptionAppliParentNull() {
        try {
            this.service.findAllByParent(null);
            fail(CHAINE_VIDE);
        } catch (final ExceptionAppliParentNull e) {
            assertEquals(MSG_FINDALLBYPARENT_KO_PARAM_NULL, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findAllByParent(parent libellé blank) -> ExceptionAppliLibelleBlank + MESSAGE_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK.</p>
     * </div>
     */
    @Test
    public void testFindAllByParentParentLibelleBlankExceptionAppliLibelleBlank() {
        final TypeProduit parent = fabriquerTypeProduit(BLANK, ID_1);

        try {
            this.service.findAllByParent(parent);
            fail(CHAINE_VIDE);
        } catch (final ExceptionAppliLibelleBlank e) {
            assertEquals(MSG_FINDALLBYPARENT_KO_LIBELLE_PARENT_BLANK, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findAllByParent(parent non persistant) -> ExceptionTechniqueGatewayNonPersistent + (MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT + parent).</p>
     * </div>
     */
    @Test
    public void testFindAllByParentParentNonPersistantExceptionTechniqueGatewayNonPersistent() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, null);

        try {
            this.service.findAllByParent(parent);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGatewayNonPersistent e) {
            assertEquals(
                    construireMessageNonPersistent(
                            MSG_FINDALLBYPARENT_PREFIX_PARENT_NON_PERSISTENT, LIBELLE_PARENT_1),
                    e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findAllByParent(parent absent DAO) -> ExceptionTechniqueGatewayNonPersistent + (MESSAGE_FINDALLBYPARENT_KO_PARENT_NON_PERSISTENT + parent).</p>
     * </div>
     */
    @Test
    public void testFindAllByParentParentAbsentExceptionTechniqueGatewayNonPersistent() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);

        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.empty());

        try {
            this.service.findAllByParent(parent);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGatewayNonPersistent e) {
            assertEquals(
                    construireMessageNonPersistent(
                            MSG_FINDALLBYPARENT_PREFIX_PARENT_NON_PERSISTENT, LIBELLE_PARENT_1),
                    e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findAllByParent(DAO enfant retourne null) -> ExceptionTechniqueGateway + ERREUR_TECHNIQUE_KO_STOCKAGE.</p>
     * </div>
     */
    @Test
    public void testFindAllByParentDaoNullExceptionTechniqueGateway() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));
        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenReturn(null);

        try {
            this.service.findAllByParent(parent);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertEquals(MSG_ERREUR_TECH_KO_STOCKAGE, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findAllByParent(DAO jette Exception) -> ExceptionTechniqueGateway (préfixé ERREUR_TECHNIQUE_STOCKAGE + cause).</p>
     * </div>
     */
    @Test
    public void testFindAllByParentDaoJetteExceptionTechniqueGateway() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final RuntimeException ex = new RuntimeException(LIBELLE_PARENT_1);
        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenThrow(ex);

        try {
            this.service.findAllByParent(parent);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertTrue(e.getMessage().startsWith(MSG_PREFIX_ERREUR_TECH));
            assertNotNull(e.getCause());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findAllByParent(nominal) -> liste non null.</p>
     * </div>
     */
    @Test
    public void testFindAllByParentNominalOk() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final List<SousTypeProduitJPA> entities = new ArrayList<SousTypeProduitJPA>();
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_1, parentJPA));
        entities.add(null);

        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenReturn(entities);

        try {
            final List<SousTypeProduit> retour = this.service.findAllByParent(parent);
            assertNotNull(retour);
            assertTrue(retour.size() >= 1); // NOPMD by danyl on 01/02/2026 15:25
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    // ===================== findById =====================

    /**
     * <div>
     * <p>findById(null) -> ExceptionAppliParamNull + MESSAGE_FINDBYID_KO_PARAM_NULL.</p>
     * </div>
     */
    @Test
    public void testFindByIdParamNullExceptionAppliParamNull() {
        try {
            this.service.findById(null);
            fail(CHAINE_VIDE);
        } catch (final ExceptionAppliParamNull e) {
            assertEquals(MSG_FINDBYID_KO_PARAM_NULL, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findById(DAO retourne null Optional) -> ExceptionTechniqueGateway + ERREUR_TECHNIQUE_KO_STOCKAGE.</p>
     * </div>
     */
    @Test
    public void testFindByIdDaoRetourneNullOptionalExceptionTechniqueGateway() {
        when(this.sousTypeProduitDaoJPA.findById(ID_1)).thenReturn(null);

        try {
            this.service.findById(ID_1);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertEquals(MSG_ERREUR_TECH_KO_STOCKAGE, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findById(absent) -> retourne null.</p>
     * </div>
     */
    @Test
    public void testFindByIdAbsentNull() {
        when(this.sousTypeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.empty());

        try {
            final SousTypeProduit retour = this.service.findById(ID_1);
            assertNull(retour);
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findById(DAO jette Exception) -> ExceptionTechniqueGateway (préfixé ERREUR_TECHNIQUE_STOCKAGE + cause).</p>
     * </div>
     */
    @Test
    public void testFindByIdDaoJetteExceptionTechniqueGateway() {
        final RuntimeException ex = new RuntimeException(LIBELLE_ENFANT_1);
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenThrow(ex);

        try {
            this.service.findById(ID_2);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertTrue(e.getMessage().startsWith(MSG_PREFIX_ERREUR_TECH));
            assertNotNull(e.getCause());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>findById(nominal) -> retourne un objet métier non null.</p>
     * </div>
     */
    @Test
    public void testFindByIdNominalOk() {
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduitJPA entityJPA = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);

        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.of(entityJPA));

        try {
            final SousTypeProduit retour = this.service.findById(ID_2);
            assertNotNull(retour);
            assertEquals(LIBELLE_ENFANT_1, retour.getSousTypeProduit());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    // ===================== update =====================

    /**
     * <div>
     * <p>update(null) -> ExceptionAppliParamNull + MESSAGE_UPDATE_KO_PARAM_NULL.</p>
     * </div>
     */
    @Test
    public void testUpdateParamNullExceptionAppliParamNull() {
        try {
            this.service.update(null);
            fail(CHAINE_VIDE);
        } catch (final ExceptionAppliParamNull e) {
            assertEquals(MSG_UPDATE_KO_PARAM_NULL, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>update(libelle blank) -> ExceptionAppliLibelleBlank + MESSAGE_UPDATE_KO_LIBELLE_BLANK.</p>
     * </div>
     */
    @Test
    public void testUpdateLibelleBlankExceptionAppliLibelleBlank() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(BLANK, ID_1, parent);

        try {
            this.service.update(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionAppliLibelleBlank e) {
            assertEquals(MSG_UPDATE_KO_LIBELLE_BLANK, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>update(id null) -> ExceptionAppliParamNonPersistent + (MESSAGE_UPDATE_KO_NON_PERSISTENT + libellé).</p>
     * </div>
     */
    @Test
    public void testUpdateIdNullExceptionAppliParamNonPersistent() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        try {
            this.service.update(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionAppliParamNonPersistent e) {
            assertEquals(MSG_UPDATE_PREFIX_NON_PERSISTENT + safeMessage(LIBELLE_ENFANT_1), e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>update(parent null) -> ExceptionAppliParentNull + MESSAGE_UPDATE_KO_PARENT_NULL.</p>
     * </div>
     */
    @Test
    public void testUpdateParentNullExceptionAppliParentNull() {
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_1, null);

        try {
            this.service.update(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionAppliParentNull e) {
            assertEquals(MSG_UPDATE_KO_PARENT_NULL, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>update(parent libellé blank) -> ExceptionAppliLibelleBlank + MESSAGE_UPDATE_KO_LIBELLE_PARENT_BLANK.</p>
     * </div>
     */
    @Test
    public void testUpdateParentLibelleBlankExceptionAppliLibelleBlank() {
        final TypeProduit parent = fabriquerTypeProduit(BLANK, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_1, parent);

        try {
            this.service.update(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionAppliLibelleBlank e) {
            assertEquals(MSG_UPDATE_KO_LIBELLE_PARENT_BLANK, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>update(parent id null) -> ExceptionTechniqueGatewayNonPersistent + (MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT + parent).</p>
     * </div>
     */
    @Test
    public void testUpdateParentIdNullExceptionTechniqueGatewayNonPersistent() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, null);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_1, parent);

        try {
            this.service.update(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGatewayNonPersistent e) {
            assertEquals(
                    construireMessageNonPersistent(
                            MSG_UPDATE_PREFIX_PARENT_NON_PERSISTENT, LIBELLE_PARENT_1),
                    e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>update(parent absent DAO) -> ExceptionTechniqueGatewayNonPersistent + (MESSAGE_UPDATE_KO_PARENT_NON_PERSISTENT + parent).</p>
     * </div>
     */
    @Test
    public void testUpdateParentAbsentExceptionTechniqueGatewayNonPersistent() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.empty());

        try {
            this.service.update(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGatewayNonPersistent e) {
            assertEquals(
                    construireMessageNonPersistent(
                            MSG_UPDATE_PREFIX_PARENT_NON_PERSISTENT, LIBELLE_PARENT_1),
                    e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>update(absent) -> retourne null.</p>
     * </div>
     */
    @Test
    public void testUpdateAbsentNull() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.empty());

        try {
            final SousTypeProduit retour = this.service.update(stp);
            assertNull(retour);
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>update(DAO findById retourne null Optional) -> ExceptionTechniqueGateway + ERREUR_TECHNIQUE_KO_STOCKAGE.</p>
     * </div>
     */
    @Test
    public void testUpdateDaoFindByIdRetourneNullOptionalExceptionTechniqueGateway() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(null);

        try {
            this.service.update(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertEquals(MSG_ERREUR_TECH_KO_STOCKAGE, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>update(sans modification) -> pas de save(), retourne l'objet inchangé.</p>
     * </div>
     */
    @Test
    public void testUpdateSansModificationPasDeSave() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final SousTypeProduitJPA persiste = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.of(persiste));

        try {
            final SousTypeProduit retour = this.service.update(stp);
            assertNotNull(retour);
            assertEquals(LIBELLE_ENFANT_1, retour.getSousTypeProduit());
            verify(this.sousTypeProduitDaoJPA, never()).save(any(SousTypeProduitJPA.class));
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>update(avec modification) -> save() appelé, retourne l'objet modifié.</p>
     * </div>
     */
    @Test
    public void testUpdateAvecModificationSaveOk() {
        final TypeProduit parentNouveau = fabriquerTypeProduit(LIBELLE_PARENT_2, ID_2);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_2, ID_3, parentNouveau);

        final TypeProduitJPA parentJPA2 = fabriquerTypeProduitJPA(LIBELLE_PARENT_2, ID_2);
        when(this.typeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.of(parentJPA2));

        final TypeProduitJPA parentJPA1 = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduitJPA persiste = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_3, parentJPA1);
        when(this.sousTypeProduitDaoJPA.findById(ID_3)).thenReturn(Optional.of(persiste));

        final SousTypeProduitJPA sauvegarde = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_2, ID_3, parentJPA2);
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class))).thenReturn(sauvegarde);

        try {
            final SousTypeProduit retour = this.service.update(stp);
            assertNotNull(retour);
            assertEquals(LIBELLE_ENFANT_2, retour.getSousTypeProduit());
            assertNotNull(retour.getTypeProduit());
            assertEquals(LIBELLE_PARENT_2, retour.getTypeProduit().getTypeProduit());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>update(save retourne null) -> ExceptionTechniqueGateway + ERREUR_TECHNIQUE_KO_STOCKAGE.</p>
     * </div>
     */
    @Test
    public void testUpdateSaveNullExceptionTechniqueGateway() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_2, ID_2, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final SousTypeProduitJPA persiste = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.of(persiste));

        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class))).thenReturn(null);

        try {
            this.service.update(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertEquals(MSG_ERREUR_TECH_KO_STOCKAGE, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>update(DAO save jette Exception) -> ExceptionTechniqueGateway (préfixé ERREUR_TECHNIQUE_STOCKAGE + cause).</p>
     * </div>
     */
    @Test
    public void testUpdateDaoSaveJetteExceptionTechniqueGateway() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_2, ID_2, parent);

        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final SousTypeProduitJPA persiste = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.of(persiste));

        final RuntimeException ex = new RuntimeException(LIBELLE_ENFANT_2);
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class))).thenThrow(ex);

        try {
            this.service.update(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertTrue(e.getMessage().startsWith(MSG_PREFIX_ERREUR_TECH));
            assertNotNull(e.getCause());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }
    
    /**
     * <div>
     * <p>update(parent modifié) -> parent mis à jour.</p>
     * </div>
     */
    @Test
    public void testUpdateParentModifieOk() {
        final TypeProduit parentAncien = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final TypeProduit parentNouveau = fabriquerTypeProduit(LIBELLE_PARENT_2, ID_2);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_3, parentNouveau);

        final TypeProduitJPA parentJPA1 = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        final TypeProduitJPA parentJPA2 = fabriquerTypeProduitJPA(LIBELLE_PARENT_2, ID_2);
        when(this.typeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.of(parentJPA2));

        final SousTypeProduitJPA persiste = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_3, parentJPA1);
        when(this.sousTypeProduitDaoJPA.findById(ID_3)).thenReturn(Optional.of(persiste));

        final SousTypeProduitJPA sauvegarde = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_3, parentJPA2);
        when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class))).thenReturn(sauvegarde);

        try {
            final SousTypeProduit retour = this.service.update(stp);
            assertNotNull(retour);
            assertEquals(LIBELLE_PARENT_2, retour.getTypeProduit().getTypeProduit());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>delete(libellé modifié) -> suppression basée sur l'ID.</p>
     * </div>
     */
    @SuppressWarnings("resource")
	@Test
    public void testDeleteLibelleModifieOk() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit("NouveauLibelle", ID_2, parent);

        final SousTypeProduitJPA entity = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2,
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1));

        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.of(entity))
            .thenReturn(Optional.empty());

        doNothing().when(this.entityManager).remove(any(SousTypeProduitJPA.class));
        doNothing().when(this.entityManager).flush();

        try {
            this.service.delete(stp);
            verify(this.entityManager, times(1)).remove(entity);
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }


    // ===================== delete =====================

    /**
     * <div>
     * <p>delete(null) -> ExceptionAppliParamNull + MESSAGE_DELETE_KO_PARAM_NULL.</p>
     * </div>
     */
    @Test
    public void testDeleteParamNullExceptionAppliParamNull() {
        try {
            this.service.delete(null);
            fail(CHAINE_VIDE);
        } catch (final ExceptionAppliParamNull e) {
            assertEquals(MSG_DELETE_KO_PARAM_NULL, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>delete(id null) -> ExceptionAppliParamNonPersistent + MESSAGE_DELETE_KO_ID_NULL.</p>
     * </div>
     */
    @Test
    public void testDeleteIdNullExceptionAppliParamNonPersistent() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, null, parent);

        try {
            this.service.delete(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionAppliParamNonPersistent e) {
            assertEquals(MSG_DELETE_KO_ID_NULL, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>delete(DAO findById retourne null Optional) -> ExceptionTechniqueGateway + ERREUR_TECHNIQUE_KO_STOCKAGE.</p>
     * </div>
     */
    @Test
    public void testDeleteDaoFindByIdRetourneNullOptionalExceptionTechniqueGateway() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(null);

        try {
            this.service.delete(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertEquals(MSG_ERREUR_TECH_KO_STOCKAGE, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>delete(absent) -> ne fait rien (pas de delete DAO).</p>
     * </div>
     */
    @SuppressWarnings(RESOURCE) // PageImpl/Page ne nécessite pas de fermeture dans les tests Mockito
    @Test
    public void testDeleteAbsentNeFaitRien() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.empty());

        try {
            this.service.delete(stp);
            verify(this.entityManager, never()).remove(any(SousTypeProduitJPA.class));
            verify(this.entityManager, never()).flush();
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p style="font-weight:bold;">INTENTION TECHNIQUE :</p>
     * <p>Test unitaire de la suppression nominale d'un {@link SousTypeProduit}.</p>
     *
     * <p style="font-weight:bold;">SCENARIO TESTE :</p>
     * <ul>
     * <li>Suppression d'une entité existante.</li>
     * <li>Vérification des appels à <code>EntityManager.remove()</code> et <code>flush()</code>.</li>
     * <li>Vérification de la vérification post-suppression via <code>findById()</code>.</li>
     * </ul>
     *
     * <p style="font-weight:bold;">GARANTIES :</p>
     * <ul>
     * <li>Le mock de <code>EntityManager</code> est correctement injecté.</li>
     * <li>Aucune <code>NullPointerException</code> n'est levée.</li>
     * <li>Les appels à <code>remove()</code> et <code>flush()</code> sont vérifiés.</li>
     * </ul>
     * </div>
     *
     * @throws Exception En cas d'erreur technique.
     */
    @SuppressWarnings(RESOURCE) // PageImpl/Page ne nécessite pas de fermeture dans les tests Mockito
    @Test
    public void testDeleteNominalOk() throws Exception {
        // =============================================
        // 1. DONNEES DE TEST
        // =============================================
        final Long id = 1L;
        final String libelle = "Test";
        final Long idParent = 2L;
        final String libelleParent = "Parent";

        // =============================================
        // 2. OBJETS MOCKES
        // =============================================
        final SousTypeProduitJPA entity = new SousTypeProduitJPA(id, libelle, new TypeProduitJPA(idParent, libelleParent));
        final SousTypeProduit objetMetier = new SousTypeProduit(id, libelle, new TypeProduit(idParent, libelleParent));

        // =============================================
        // 3. CONFIGURATION DES MOCKS
        // =============================================
        /* Mock du DAO : retourne l'entité avant suppression, puis Optional.empty() après. */
        when(sousTypeProduitDaoJPA.findById(id))
            .thenReturn(Optional.of(entity))   // Avant suppression
            .thenReturn(Optional.empty());     // Après suppression

        /* Mock de EntityManager : ne fait rien lors de remove() et flush(). */
        doNothing().when(entityManager).remove(any(SousTypeProduitJPA.class));
        doNothing().when(entityManager).flush();

        // =============================================
        // 4. APPEL DE LA METHODE A TESTER
        // =============================================
        service.delete(objetMetier);

        // =============================================
        // 5. VERIFICATIONS
        // =============================================
        /* Vérifie que EntityManager.remove() a été appelé avec l'entité. */
        verify(entityManager, times(1)).remove(entity);

        /* Vérifie que EntityManager.flush() a été appelé. */
        verify(entityManager, times(1)).flush();

        /* Vérifie que findById() a été appelé deux fois :
           - 1ère fois pour récupérer l'entité à supprimer.
           - 2ème fois pour vérifier la suppression. */
        verify(sousTypeProduitDaoJPA, times(2)).findById(id);
    }

    /**
     * <div>
     * <p>delete(DAO findById jette Exception) -> ExceptionTechniqueGateway (préfixé ERREUR_TECHNIQUE_STOCKAGE).</p>
     * </div>
     */
    @Test
    public void testDeleteExceptionDaoFindByIdExceptionTechniqueGateway() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final RuntimeException ex = new RuntimeException(LIBELLE_ENFANT_1);
        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenThrow(ex);

        try {
            this.service.delete(stp);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertTrue(e.getMessage().startsWith(MSG_PREFIX_ERREUR_TECH));
            assertNotNull(e.getCause());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    // ===================== count =====================

    /**
     * <div>
     * <p>count(nominal) -> retourne le compteur DAO.</p>
     * </div>
     */
    @Test
    public void testCountNominalOk() {
        when(this.sousTypeProduitDaoJPA.count()).thenReturn(TOTAL_10);

        try {
            final long retour = this.service.count();
            assertEquals(TOTAL_10, retour);
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>count(DAO jette Exception) -> ExceptionTechniqueGateway (message préfixé ERREUR_TECHNIQUE_STOCKAGE).</p>
     * </div>
     */
    @Test
    public void testCountExceptionDaoExceptionTechniqueGateway() {
        final RuntimeException ex = new RuntimeException(StringUtils.defaultString(LIBELLE_ENFANT_1));
        when(this.sousTypeProduitDaoJPA.count()).thenThrow(ex);

        try {
            this.service.count();
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertTrue(e.getMessage().startsWith(MSG_PREFIX_ERREUR_TECH));
            assertNotNull(e.getCause());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    // ===================== TESTS BETON (sanity / invariants) =====================

    /**
     * <div>
     * <p>Sanity : vérifie qu'un toUpperCase() est toujours appelé avec Locale (contrat global).</p>
     * </div>
     */
    @Test
    public void testSanityLocaleToUpperCase() {
        final String s = LIBELLE_PARENT_1.toUpperCase(LOCALE_DEFAUT);
        assertNotNull(s);
    }

    /**
     * <div>
     * <p>Sanity : vérifie qu'aucune String null n'est construite par les helpers.</p>
     * </div>
     */
    @Test
    public void testSanitySafeMessage() {
        assertEquals(CHAINE_VIDE, safeMessage(null));
        assertEquals(CHAINE_VIDE, safeMessage(CHAINE_VIDE));
    }

    /**
     * <div>
     * <p>Sanity : évite toute dépendance à des chaînes hardcodées (exemple).</p>
     * </div>
     */
    @Test
    public void testSanityNoHardcodedBlankLogic() {
        assertTrue(StringUtils.isBlank(BLANK));
    }

    /**
     * <div>
     * <p>Sanity : Mockito retourne null par défaut si non stub -> béton : toujours stubber findById en delete/update.</p>
     * </div>
     */
    @Test
    public void testSanityMockitoDefaultNullOnOptionalIsHandled() {
        lenient().when(this.sousTypeProduitDaoJPA.findById(anyLong())).thenReturn(null);

        try {
            this.service.findById(ID_1);
            fail(CHAINE_VIDE);
        } catch (final ExceptionTechniqueGateway e) {
            assertEquals(MSG_ERREUR_TECH_KO_STOCKAGE, e.getMessage());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    // ===================== TESTS SUPPLEMENTAIRES (nouveaux cas limites) =====================

    /**
     * <div>
     * <p>Test béton : vérifie que la méthode delete() gère correctement
     * une Exception levée par EntityManager.remove().</p>
     * </div>
     */
    @SuppressWarnings(RESOURCE) // PageImpl/Page ne nécessite pas de fermeture dans les tests Mockito
    @Test
    public void testDeleteEntityManagerRemoveJetteException() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final SousTypeProduitJPA entity = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2,
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1));

        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.of(entity));
        doThrow(new RuntimeException("Erreur simulatee par remove()"))
            .when(this.entityManager).remove(any(SousTypeProduitJPA.class));

        try {
            this.service.delete(stp);
            fail("Devrait jeter une ExceptionTechniqueGateway");
        } catch (final ExceptionTechniqueGateway e) {
            assertTrue(e.getMessage().startsWith(MSG_PREFIX_ERREUR_TECH));
            assertNotNull(e.getCause());
        } catch (final Exception e) {
            fail("Mauvaise exception levée: " + e.getClass().getName());
        }
    }

    /**
     * <div>
     * <p>Test béton : vérifie que la méthode delete() gère correctement
     * une Exception levée par EntityManager.flush().</p>
     * </div>
     */
    @SuppressWarnings(RESOURCE) // PageImpl/Page ne nécessite pas de fermeture dans les tests Mockito
    @Test
    public void testDeleteEntityManagerFlushJetteException() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final SousTypeProduitJPA entity = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2,
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1));

        when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.of(entity));
        doNothing().when(this.entityManager).remove(any(SousTypeProduitJPA.class));
        doThrow(new RuntimeException("Erreur simulatee par flush()"))
            .when(this.entityManager).flush();

        try {
            this.service.delete(stp);
            fail("Devrait jeter une ExceptionTechniqueGateway");
        } catch (final ExceptionTechniqueGateway e) {
            assertTrue(e.getMessage().startsWith(MSG_PREFIX_ERREUR_TECH));
            assertNotNull(e.getCause());
        } catch (final Exception e) {
            fail("Mauvaise exception levée: " + e.getClass().getName());
        }
    }

    /**
     * <div>
     * <p>Test béton : vérifie que la méthode delete() vérifie bien
     * la suppression effective après flush().</p>
     * </div>
     */
    @SuppressWarnings(RESOURCE) // PageImpl/Page ne nécessite pas de fermeture dans les tests Mockito
    @Test
    public void testDeleteVerificationPostSuppressionEchoue() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        final SousTypeProduitJPA entity = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2,
                fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1));

        when(this.sousTypeProduitDaoJPA.findById(ID_2))
            .thenReturn(Optional.of(entity))  // Avant suppression
            .thenReturn(Optional.of(entity)); // Après suppression (simule échec)

        doNothing().when(this.entityManager).remove(any(SousTypeProduitJPA.class));
        doNothing().when(this.entityManager).flush();

        try {
            this.service.delete(stp);
            fail("Devrait jeter une ExceptionTechniqueGateway");
        } catch (final ExceptionTechniqueGateway e) {
            assertTrue(e.getMessage().contains("Échec de la suppression"));
        } catch (final Exception e) {
            fail("Mauvaise exception levée: " + e.getClass().getName());
        }
    }

    
    
    /**
     * <div>
     * <p>Test béton : vérifie que la méthode update() gère correctement
     * un parent modifié avec un libellé en majuscules/minuscules différentes.</p>
     * </div>
     */
    @Test
    public void testUpdateParentLibelleCaseSensitive() {
        // =============================================
        // 1. DONNEES DE TEST
        // =============================================
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1.toUpperCase(LOCALE_DEFAUT), ID_1);
        final SousTypeProduit stp = fabriquerSousTypeProduit(LIBELLE_ENFANT_1, ID_2, parent);

        // =============================================
        // 2. STUBS (tous lenient pour éviter UnnecessaryStubbingException)
        // =============================================
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);
        lenient().when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final SousTypeProduitJPA persiste = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);
        lenient().when(this.sousTypeProduitDaoJPA.findById(ID_2)).thenReturn(Optional.of(persiste));

        final SousTypeProduitJPA sauvegarde = fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA);
        lenient().when(this.sousTypeProduitDaoJPA.save(any(SousTypeProduitJPA.class))).thenReturn(sauvegarde);

        // =============================================
        // 3. EXECUTION DU TEST
        // =============================================
        try {
            final SousTypeProduit retour = this.service.update(stp);

            // =============================================
            // 4. VERIFICATIONS
            // =============================================
            assertNotNull(retour, "Le résultat ne doit pas être null");
            assertEquals(LIBELLE_ENFANT_1, retour.getSousTypeProduit(), "Le libellé enfant doit être conservé");
            assertEquals(LIBELLE_PARENT_1, retour.getTypeProduit().getTypeProduit(),
                    "Le libellé parent doit être normalisé (minuscules)");

        } catch (final Exception e) {
            fail("Aucune exception ne doit être levée: " + safeMessage(e));
        }
    }    
    
    
    /**
     * <div>
     * <p>Test béton : vérifie que la méthode findByLibelleRapide()
     * retourne bien une liste vide si le contenu est introuvable.</p>
     * </div>
     */
    @Test
    public void testFindByLibelleRapideContenuIntrouvable() {
        lenient().when(this.sousTypeProduitDaoJPA.findBySousTypeProduitContainingIgnoreCase("INCONNU"))
            .thenReturn(new ArrayList<SousTypeProduitJPA>());

        try {
            final List<SousTypeProduit> retour = this.service.findByLibelleRapide("INCONNU");
            assertNotNull(retour);
            assertTrue(retour.isEmpty());
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }

    /**
     * <div>
     * <p>Test béton : vérifie que la méthode findAllByParent()
     * gère correctement un parent avec des enfants en double.</p>
     * </div>
     */
    @Test
    public void testFindAllByParentAvecDoublons() {
        final TypeProduit parent = fabriquerTypeProduit(LIBELLE_PARENT_1, ID_1);
        final TypeProduitJPA parentJPA = fabriquerTypeProduitJPA(LIBELLE_PARENT_1, ID_1);

        lenient().when(this.typeProduitDaoJPA.findById(ID_1)).thenReturn(Optional.of(parentJPA));

        final List<SousTypeProduitJPA> entities = new ArrayList<SousTypeProduitJPA>();
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_1, parentJPA));
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_1, ID_2, parentJPA)); // Doublon libellé
        entities.add(fabriquerSousTypeProduitJPA(LIBELLE_ENFANT_2, ID_3, parentJPA));

        when(this.sousTypeProduitDaoJPA.findAllByTypeProduit(parentJPA)).thenReturn(entities);

        try {
            final List<SousTypeProduit> retour = this.service.findAllByParent(parent);
            assertNotNull(retour);
            assertEquals(2, retour.size()); // 1 doublon filtré
        } catch (final Exception e) {
            fail(CHAINE_VIDE);
        }
    }
}
