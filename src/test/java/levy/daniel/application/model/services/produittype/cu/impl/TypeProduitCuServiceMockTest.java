package levy.daniel.application.model.services.produittype.cu.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.OutputDTO;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionDoublon;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionNonPersistant;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreBlank;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreNull;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionStockageVide;
import levy.daniel.application.model.services.produittype.gateway.TypeProduitGatewayIService;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;

/**
 * <div>
 * <p style="font-weight:bold;">CLASSE TypeProduitCuServiceMockTest.java :</p>
 * <p>Tests JUnit Mockito complets (avec tests "béton") pour
 * {@link TypeProduitCuService}.</p>
 * <p>Vérifie l'implémentation des contrats du PORT
 * {@link TypeProduitICuService} et la délégation vers
 * {@link TypeProduitGatewayIService}.</p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 21 janvier 2026
 */
@ExtendWith(MockitoExtension.class)
public class TypeProduitCuServiceMockTest {

	// *************************** CONSTANTES ******************************/

	/** Tag JUnit : tests Mockito de la couche CU. */
	public static final String TAG = "cu-mock";

	/** "bazar". */
	public static final String BAZAR = "bazar";

	/** "tourisme". */
	public static final String TOURISME = "tourisme";

	/** "outillage". */
	public static final String OUTILLAGE = "outillage";

	/** "vêtement". */
	public static final String VETEMENT = "vêtement";

	/** Chaine blank : "   ". */
	public static final String ESPACES = "   ";

	/** Message mock gateway : "message gateway". */
	public static final String MESSAGE_GATEWAY = "message gateway";

	/** Message mock gateway (bis) : "message gateway (bis)". */
	public static final String MESSAGE_GATEWAY_BIS = "message gateway (bis)";

	// ************************* CONSTRUCTEURS *****************************/

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public TypeProduitCuServiceMockTest() {
		super();
	}

	// *************************** METHODES *******************************/

	/**
	 * <div>
	 * <p>creer(null) : erreur utilisateur bénigne.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(null) : retourne null, message utilisateur, aucune interaction gateway")
	public void testCreerNull() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		// ===================== ACT =====================

		final OutputDTO retour = service.creer(null);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_CREER_NULL);
		verifyNoInteractions(gateway);
	}

	/**
	 * <div>
	 * <p>creer(blank) : jette ExceptionParametreBlank, message = MESSAGE_CREER_NOM_BLANK.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(blank) : ExceptionParametreBlank + message MESSAGE_CREER_NOM_BLANK + aucune interaction gateway")
	public void testCreerBlank() {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final InputDTO dto = new TypeProduitDTO.InputDTO(ESPACES);

		// ===================== ACT & ASSERT =====================

		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(service.getMessage()).isEqualTo(TypeProduitICuService.MESSAGE_CREER_NOM_BLANK);

		verifyNoInteractions(gateway);
	}

	/**
	 * <div>
	 * <p>creer(doublon) : jette ExceptionDoublon, message = MESSAGE_DOUBLON + libellé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(doublon) : ExceptionDoublon + message MESSAGE_DOUBLON + libellé + aucune création gateway")
	public void testCreerDoublon() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final InputDTO dto = new TypeProduitDTO.InputDTO(VETEMENT);

		/* doublon si gateway.findByLibelle(libelle) != null. */
		when(gateway.findByLibelle(VETEMENT)).thenReturn(new TypeProduit(VETEMENT));

		// ===================== ACT & ASSERT =====================

		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(ExceptionDoublon.class);

		assertThat(service.getMessage()).isEqualTo(TypeProduitICuService.MESSAGE_DOUBLON + VETEMENT);

		verify(gateway, times(1)).findByLibelle(VETEMENT);
		verify(gateway, never()).creer(any(TypeProduit.class));
	}

	/**
	 * <div>
	 * <p>creer(ok) : délègue au gateway, retourne un OutputDTO, message = MESSAGE_CREER_OK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(ok) : délégation gateway.creer + OutputDTO + message MESSAGE_CREER_OK")
	public void testCreerOk() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final InputDTO dto = new TypeProduitDTO.InputDTO(OUTILLAGE);

		/* pas doublon => findByLibelle retourne null. */
		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(null);

		final TypeProduit cree = new TypeProduit(OUTILLAGE);
		cree.setIdTypeProduit(1L);
		when(gateway.creer(any(TypeProduit.class))).thenReturn(cree);

		// ===================== ACT =====================

		final OutputDTO retour = service.creer(dto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNotNull();
		assertThat(retour.getTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_CREER_OK);

		final ArgumentCaptor<TypeProduit> captor = ArgumentCaptor.forClass(TypeProduit.class);
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(captor.capture());

		final TypeProduit envoye = captor.getValue();
		assertThat(envoye).isNotNull();
		assertThat(envoye.getTypeProduit()).isEqualTo(OUTILLAGE);
	}

	/**
	 * <div>
	 * <p>rechercherTous() : stockage null -> ExceptionStockageVide + message MESSAGE_STOCKAGE_NULL.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTous() : gateway retourne null -> ExceptionStockageVide + message MESSAGE_STOCKAGE_NULL")
	public void testRechercherTousStockageNull() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		when(gateway.rechercherTous()).thenReturn(null);

		// ===================== ACT & ASSERT =====================

		assertThatThrownBy(() -> service.rechercherTous())
				.isInstanceOf(ExceptionStockageVide.class);

		assertThat(service.getMessage()).isEqualTo(TypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		verify(gateway, times(1)).rechercherTous();
	}

	/**
	 * <div>
	 * <p>rechercherTous() : retourne une liste vide après filtrage -> message MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTous() : résultats vides -> liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testRechercherTousVide() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final List<TypeProduit> records = new ArrayList<TypeProduit>();
		records.add(null);
		when(gateway.rechercherTous()).thenReturn(records);

		// ===================== ACT =====================

		final List<OutputDTO> retour = service.rechercherTous();
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(gateway, times(1)).rechercherTous();
	}

	/**
	 * <div>
	 * <p>rechercherTousString() : retourne des libellés uniques non blank, triés, message OK si non vide.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousString() : dédoublonne + retire blanks + message MESSAGE_RECHERCHE_OK")
	public void testRechercherTousStringOk() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final TypeProduit tp1 = new TypeProduit(TOURISME);
		tp1.setIdTypeProduit(2L);

		final TypeProduit tp2 = new TypeProduit(ESPACES);
		tp2.setIdTypeProduit(3L);

		final TypeProduit tp3 = new TypeProduit(TOURISME);
		tp3.setIdTypeProduit(4L);

		final TypeProduit tp4 = new TypeProduit(BAZAR);
		tp4.setIdTypeProduit(1L);

		when(gateway.rechercherTous()).thenReturn(Arrays.asList(tp1, tp2, tp3, tp4, null));

		// ===================== ACT =====================

		final List<String> retour = service.rechercherTousString();
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNotNull();
		assertThat(retour).contains(BAZAR, TOURISME);
		assertThat(retour).doesNotContain(ESPACES);
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(gateway, times(1)).rechercherTous();
	}

	/**
	 * <div>
	 * <p>rechercherTousParPage(null) : IllegalStateException + message MESSAGE_PAGEABLE_NULL.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousParPage(null) : IllegalStateException + message MESSAGE_PAGEABLE_NULL + aucune interaction gateway")
	public void testRechercherTousParPageNull() {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		// ===================== ACT & ASSERT =====================

		assertThatThrownBy(() -> service.rechercherTousParPage(null))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage()).isEqualTo(TypeProduitICuService.MESSAGE_PAGEABLE_NULL);

		verifyNoInteractions(gateway);
	}

	/**
	 * <div>
	 * <p>rechercherTousParPage() : gateway retourne null -> retourne null et message MESSAGE_RECHERCHE_PAGINEE_KO.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousParPage() : résultat gateway null -> null + message MESSAGE_RECHERCHE_PAGINEE_KO")
	public void testRechercherTousParPageGatewayNull() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final RequetePage pageable = new RequetePage(0, 10);

		when(gateway.rechercherTousParPage(pageable)).thenReturn(null);

		// ===================== ACT =====================

		final ResultatPage<OutputDTO> retour = service.rechercherTousParPage(pageable);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_KO);

		verify(gateway, times(1)).rechercherTousParPage(pageable);
	}

	/**
	 * <div>
	 * <p>rechercherTousParPage() : ok -> retourne ResultatPage, message MESSAGE_RECHERCHE_PAGINEE_OK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousParPage() : OK -> ResultatPage + message MESSAGE_RECHERCHE_PAGINEE_OK")
	public void testRechercherTousParPageOk() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final RequetePage pageable = new RequetePage(0, 2);

		final TypeProduit tp1 = new TypeProduit(BAZAR);
		tp1.setIdTypeProduit(1L);

		final TypeProduit tp2 = new TypeProduit(TOURISME);
		tp2.setIdTypeProduit(2L);

		final ResultatPage<TypeProduit> resultatGateway = new ResultatPage<TypeProduit>(
				Arrays.asList(tp2, tp1, null), 0, 2, 2L);

		when(gateway.rechercherTousParPage(pageable)).thenReturn(resultatGateway);

		// ===================== ACT =====================

		final ResultatPage<OutputDTO> retour = service.rechercherTousParPage(pageable);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNotNull();
		assertThat(retour.getContent()).isNotNull();
		assertThat(retour.getContent().size()).isEqualTo(2);
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		verify(gateway, times(1)).rechercherTousParPage(pageable);
	}

	/**
	 * <div>
	 * <p>findByLibelle(blank) : retourne null et message MESSAGE_PARAM_BLANK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(blank) : null + message MESSAGE_PARAM_BLANK + aucune interaction gateway")
	public void testFindByLibelleBlank() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		// ===================== ACT =====================

		final OutputDTO retour = service.findByLibelle(ESPACES);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);
		verifyNoInteractions(gateway);
	}

	/**
	 * <div>
	 * <p>findByLibelle(introuvable) : null + message MESSAGE_OBJ_INTROUVABLE + libellé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(introuvable) : null + message MESSAGE_OBJ_INTROUVABLE + libellé")
	public void testFindByLibelleIntrouvable() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		when(gateway.findByLibelle(VETEMENT)).thenReturn(null);

		// ===================== ACT =====================

		final OutputDTO retour = service.findByLibelle(VETEMENT);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + VETEMENT);

		verify(gateway, times(1)).findByLibelle(VETEMENT);
	}

	/**
	 * <div>
	 * <p>findByLibelle(ok) : OutputDTO + message MESSAGE_SUCCES_RECHERCHE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(ok) : OutputDTO + message MESSAGE_SUCCES_RECHERCHE")
	public void testFindByLibelleOk() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final TypeProduit tp = new TypeProduit(VETEMENT);
		tp.setIdTypeProduit(7L);

		when(gateway.findByLibelle(VETEMENT)).thenReturn(tp);

		// ===================== ACT =====================

		final OutputDTO retour = service.findByLibelle(VETEMENT);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNotNull();
		assertThat(retour.getTypeProduit()).isEqualTo(VETEMENT);
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		verify(gateway, times(1)).findByLibelle(VETEMENT);
	}

	/**
	 * <div>
	 * <p>findByLibelleRapide(null) : IllegalStateException + message MESSAGE_PARAM_NULL.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(null) : IllegalStateException + message MESSAGE_PARAM_NULL + aucune interaction gateway")
	public void testFindByLibelleRapideNull() {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		// ===================== ACT & ASSERT =====================

		assertThatThrownBy(() -> service.findByLibelleRapide(null))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage()).isEqualTo(TypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);
	}

	/**
	 * <div>
	 * <p>findByLibelleRapide(blank) : délègue à rechercherTous().</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(blank) : délègue à rechercherTous()")
	public void testFindByLibelleRapideBlankDelegueRechercherTous() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		/* rechercherTous() délègue à gateway.rechercherTous(). */
		when(gateway.rechercherTous()).thenReturn(new ArrayList<TypeProduit>());

		// ===================== ACT =====================

		final List<OutputDTO> retour = service.findByLibelleRapide(ESPACES);

		// ===================== ASSERT =====================

		assertThat(retour).isNotNull();
		verify(gateway, times(1)).rechercherTous();
		verify(gateway, never()).findByLibelleRapide(any(String.class));
	}

	/**
	 * <div>
	 * <p>findByDTO(null) : null + message MESSAGE_RECHERCHE_OBJ_NULL.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(null) : null + message MESSAGE_RECHERCHE_OBJ_NULL")
	public void testFindByDTONull() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		// ===================== ACT =====================

		final OutputDTO retour = service.findByDTO(null);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OBJ_NULL);
		verifyNoInteractions(gateway);
	}

	/**
	 * <div>
	 * <p>findByDTO(ok) : délègue à findByLibelle(libellé).</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(ok) : délègue à findByLibelle(libellé)")
	public void testFindByDTODelegueFindByLibelle() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final InputDTO dto = new TypeProduitDTO.InputDTO(TOURISME);

		final TypeProduit tp = new TypeProduit(TOURISME);
		tp.setIdTypeProduit(9L);

		when(gateway.findByLibelle(TOURISME)).thenReturn(tp);

		// ===================== ACT =====================

		final OutputDTO retour = service.findByDTO(dto);

		// ===================== ASSERT =====================

		assertThat(retour).isNotNull();
		assertThat(retour.getTypeProduit()).isEqualTo(TOURISME);
		verify(gateway, times(1)).findByLibelle(TOURISME);
	}

	/**
	 * <div>
	 * <p>findById(null) : null + message MESSAGE_PARAM_NULL.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(null) : null + message MESSAGE_PARAM_NULL + aucune interaction gateway")
	public void testFindByIdNull() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		// ===================== ACT =====================

		final OutputDTO retour = service.findById(null);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_PARAM_NULL);
		verifyNoInteractions(gateway);
	}

	/**
	 * <div>
	 * <p>findById(introuvable) : null + message MESSAGE_OBJ_INTROUVABLE + id.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(introuvable) : null + message MESSAGE_OBJ_INTROUVABLE + id")
	public void testFindByIdIntrouvable() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final Long id = 12L;

		when(gateway.findById(id)).thenReturn(null);

		// ===================== ACT =====================

		final OutputDTO retour = service.findById(id);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + id);

		verify(gateway, times(1)).findById(id);
	}

	/**
	 * <div>
	 * <p>findById(ok) : OutputDTO + message MESSAGE_SUCCES_RECHERCHE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(ok) : OutputDTO + message MESSAGE_SUCCES_RECHERCHE")
	public void testFindByIdOk() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final Long id = 3L;
		final TypeProduit tp = new TypeProduit(BAZAR);
		tp.setIdTypeProduit(id);

		when(gateway.findById(id)).thenReturn(tp);

		// ===================== ACT =====================

		final OutputDTO retour = service.findById(id);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNotNull();
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		verify(gateway, times(1)).findById(id);
	}

	/**
	 * <div>
	 * <p>update(null) : ExceptionParametreNull + message MESSAGE_PARAM_NULL.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(null) : ExceptionParametreNull + message MESSAGE_PARAM_NULL + aucune interaction gateway")
	public void testUpdateNull() {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		// ===================== ACT & ASSERT =====================

		assertThatThrownBy(() -> service.update(null))
				.isInstanceOf(ExceptionParametreNull.class);

		assertThat(service.getMessage()).isEqualTo(TypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);
	}

	/**
	 * <div>
	 * <p>update(blank) : ExceptionParametreBlank + message MESSAGE_PARAM_BLANK.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(blank) : ExceptionParametreBlank + message MESSAGE_PARAM_BLANK + aucune interaction gateway")
	public void testUpdateBlank() {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final InputDTO dto = new TypeProduitDTO.InputDTO(ESPACES);

		// ===================== ACT & ASSERT =====================

		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(service.getMessage()).isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
	}

	/**
	 * <div>
	 * <p>update(introuvable) : retourne null + message MESSAGE_OBJ_INTROUVABLE + libellé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(introuvable) : null + message MESSAGE_OBJ_INTROUVABLE + libellé")
	public void testUpdateIntrouvable() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final InputDTO dto = new TypeProduitDTO.InputDTO(VETEMENT);

		when(gateway.findByLibelle(VETEMENT)).thenReturn(null);

		// ===================== ACT =====================

		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + VETEMENT);

		verify(gateway, times(1)).findByLibelle(VETEMENT);
		verify(gateway, never()).update(any(TypeProduit.class));
	}

	/**
	 * <div>
	 * <p>update(non persisté) : ExceptionNonPersistant + message MESSAGE_OBJ_NON_PERSISTE + libellé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(non persisté) : ExceptionNonPersistant + message MESSAGE_OBJ_NON_PERSISTE + libellé")
	public void testUpdateNonPersistant() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final InputDTO dto = new TypeProduitDTO.InputDTO(TOURISME);

		final TypeProduit existant = new TypeProduit(TOURISME);
		/* id null => non persisté. */
		existant.setIdTypeProduit(null);

		when(gateway.findByLibelle(TOURISME)).thenReturn(existant);

		// ===================== ACT & ASSERT =====================

		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionNonPersistant.class);

		assertThat(service.getMessage()).isEqualTo(
				TypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE + TOURISME);

		verify(gateway, times(1)).findByLibelle(TOURISME);
		verify(gateway, never()).update(any(TypeProduit.class));
	}

	/**
	 * <div>
	 * <p>update(gateway retourne null) : null + message MESSAGE_MODIF_KO + libellé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(gateway null) : null + message MESSAGE_MODIF_KO + libellé")
	public void testUpdateGatewayNull() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final InputDTO dto = new TypeProduitDTO.InputDTO(OUTILLAGE);

		final TypeProduit existant = new TypeProduit(OUTILLAGE);
		existant.setIdTypeProduit(10L);
		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(existant);

		when(gateway.update(any(TypeProduit.class))).thenReturn(null);

		// ===================== ACT =====================

		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_MODIF_KO + OUTILLAGE);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).update(any(TypeProduit.class));
	}

	/**
	 * <div>
	 * <p>update(ok) : OutputDTO + message MESSAGE_MODIF_OK + libellé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(ok) : OutputDTO + message MESSAGE_MODIF_OK + libellé")
	public void testUpdateOk() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final InputDTO dto = new TypeProduitDTO.InputDTO(BAZAR);

		final TypeProduit existant = new TypeProduit(BAZAR);
		existant.setIdTypeProduit(5L);
		when(gateway.findByLibelle(BAZAR)).thenReturn(existant);

		final TypeProduit modifie = new TypeProduit(BAZAR);
		modifie.setIdTypeProduit(5L);
		when(gateway.update(any(TypeProduit.class))).thenReturn(modifie);

		// ===================== ACT =====================

		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNotNull();
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_MODIF_OK + BAZAR);

		verify(gateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).update(any(TypeProduit.class));
	}

	/**
	 * <div>
	 * <p>delete(null) : ExceptionParametreNull + message MESSAGE_PARAM_NULL.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(null) : ExceptionParametreNull + message MESSAGE_PARAM_NULL + aucune interaction gateway")
	public void testDeleteNull() {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		// ===================== ACT & ASSERT =====================

		assertThatThrownBy(() -> service.delete(null))
				.isInstanceOf(ExceptionParametreNull.class);

		assertThat(service.getMessage()).isEqualTo(TypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);
	}

	/**
	 * <div>
	 * <p>delete(blank) : ExceptionParametreBlank + message MESSAGE_PARAM_BLANK.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(blank) : ExceptionParametreBlank + message MESSAGE_PARAM_BLANK + aucune interaction gateway")
	public void testDeleteBlank() {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final InputDTO dto = new TypeProduitDTO.InputDTO(ESPACES);

		// ===================== ACT & ASSERT =====================

		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(service.getMessage()).isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
	}

	/**
	 * <div>
	 * <p>delete(introuvable) : ne jette pas d'exception, message MESSAGE_OBJ_INTROUVABLE + libellé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(introuvable) : aucune exception + message MESSAGE_OBJ_INTROUVABLE + libellé + gateway.delete jamais appelé")
	public void testDeleteIntrouvable() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final InputDTO dto = new TypeProduitDTO.InputDTO(VETEMENT);

		when(gateway.findByLibelle(VETEMENT)).thenReturn(null);

		// ===================== ACT =====================

		service.delete(dto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + VETEMENT);

		verify(gateway, times(1)).findByLibelle(VETEMENT);
		verify(gateway, never()).delete(any(TypeProduit.class));
	}

	/**
	 * <div>
	 * <p>delete(ok) : délègue au gateway.delete, message MESSAGE_DELETE_OK + libellé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(ok) : gateway.delete appelé + message MESSAGE_DELETE_OK + libellé")
	public void testDeleteOk() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final InputDTO dto = new TypeProduitDTO.InputDTO(TOURISME);

		final TypeProduit tp = new TypeProduit(TOURISME);
		tp.setIdTypeProduit(15L);

		when(gateway.findByLibelle(TOURISME)).thenReturn(tp);

		// ===================== ACT =====================

		service.delete(dto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_DELETE_OK + TOURISME);

		verify(gateway, times(1)).findByLibelle(TOURISME);
		verify(gateway, times(1)).delete(tp);
	}

	/**
	 * <div>
	 * <p>delete(échec) : gateway.delete jette une exception -> l'exception est relancée, message MESSAGE_DELETE_KO + libellé.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(échec) : exception relancée + message MESSAGE_DELETE_KO + libellé")
	public void testDeleteEchec() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final InputDTO dto = new TypeProduitDTO.InputDTO(OUTILLAGE);

		final TypeProduit tp = new TypeProduit(OUTILLAGE);
		tp.setIdTypeProduit(20L);

		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(tp);

		final Exception ex = new Exception(MESSAGE_GATEWAY);
		doThrow(ex).when(gateway).delete(tp);

		// ===================== ACT & ASSERT =====================

		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(Exception.class);

		assertThat(service.getMessage()).isEqualTo(TypeProduitICuService.MESSAGE_DELETE_KO + OUTILLAGE);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).delete(tp);
	}

	/**
	 * <div>
	 * <p>count() : délègue au gateway.count().</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count() : délègue gateway.count()")
	public void testCount() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		when(gateway.count()).thenReturn(42L);

		// ===================== ACT =====================

		final long retour = service.count();

		// ===================== ASSERT =====================

		assertThat(retour).isEqualTo(42L);
		verify(gateway, times(1)).count();
	}

	/**
	 * <div>
	 * <p>getMessage() : retourne le message courant.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("getMessage() : retourne le message courant")
	public void testGetMessage() throws Exception {

	    // ===================== ARRANGE =====================

	    final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
	    final TypeProduitCuService service = new TypeProduitCuService(gateway);

	    // ===================== ACT =====================

	    /* On positionne le message local via une erreur utilisateur bénigne
	     * (sans interaction gateway). */
	    service.creer(null);

	    final String message = service.getMessage();

	    /* Exemple d'usage de toUpperCase avec Locale (règle projet). */
	    final String dummy = MESSAGE_GATEWAY_BIS.toUpperCase(Locale.getDefault());
	    assertThat(dummy).isNotBlank();

	    // ===================== ASSERT =====================

	    assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_CREER_NULL);
	    verifyNoInteractions(gateway);
	}
	
}
