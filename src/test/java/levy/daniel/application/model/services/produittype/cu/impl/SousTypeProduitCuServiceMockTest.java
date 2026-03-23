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

import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO.OutputDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.cu.SousTypeProduitICuService;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionDoublon;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionNonPersistant;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreBlank;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreNull;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionStockageVide;
import levy.daniel.application.model.services.produittype.gateway.SousTypeProduitGatewayIService;
import levy.daniel.application.model.services.produittype.gateway.TypeProduitGatewayIService;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;

/**
 * <div>
 * <p style="font-weight:bold;">CLASSE SousTypeProduitCuServiceMockTest.java :</p>
 * <p>Tests JUnit Mockito complets (avec tests "béton") pour
 * {@link SousTypeProduitCuService}.</p>
 * <p>Vérifie l'implémentation des contrats du PORT
 * {@link SousTypeProduitICuService} et la délégation vers
 * {@link SousTypeProduitGatewayIService}.</p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 22 janvier 2026
 */
@ExtendWith(MockitoExtension.class)
public class SousTypeProduitCuServiceMockTest {

	// *************************** CONSTANTES ******************************/

	/** Tag JUnit : tests Mockito de la couche CU. */
	public static final String TAG = "cu-mock";

	/** TypeProduit parent : "bazar". */
	public static final String BAZAR = "bazar";

	/** TypeProduit parent : "tourisme". */
	public static final String TOURISME = "tourisme";

	/** SousTypeProduit : "outillage". */
	public static final String OUTILLAGE = "outillage";

	/** SousTypeProduit : "vêtement". */
	public static final String VETEMENT = "vêtement";

	/** Contenu recherche rapide : "tou". */
	public static final String TOU = "tou";

	/** SousTypeProduit recherche rapide : "tourisme-a". */
	public static final String TOURISME_A = "tourisme-a";

	/** SousTypeProduit recherche rapide : "tourisme-b". */
	public static final String TOURISME_B = "tourisme-b";

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
	public SousTypeProduitCuServiceMockTest() {
		super();
	}

	// ============================ TESTS creer(...) =======================

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

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		// ===================== ACT =====================

		final OutputDTO retour = service.creer(null);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);
	}

	/**
	 * <div>
	 * <p>creer(dto) : libellé blank -> ExceptionParametreBlank + message MESSAGE_CREER_NOM_BLANK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(blank) : ExceptionParametreBlank + message MESSAGE_CREER_NOM_BLANK")
	public void testCreerBlank() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, ESPACES);

		// ===================== ACT =====================

		assertThatThrownBy(() -> service.creer(dto))
			.isInstanceOf(ExceptionParametreBlank.class);

		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_NOM_BLANK);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);
	}

	/**
	 * <div>
	 * <p>creer(dto) : doublon -> ExceptionDoublon + message MESSAGE_DOUBLON + libellé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(doublon) : ExceptionDoublon + message MESSAGE_DOUBLON")
	public void testCreerDoublon() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, new TypeProduit(BAZAR));
		existant.setIdSousTypeProduit(1L);

		when(gateway.findByLibelle(OUTILLAGE).get(0)).thenReturn(existant);

		// ===================== ACT =====================

		assertThatThrownBy(() -> service.creer(dto))
			.isInstanceOf(ExceptionDoublon.class);

		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_DOUBLON + OUTILLAGE);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).creer(any(SousTypeProduit.class));
		verifyNoInteractions(typeProduitGateway);
	}

	/**
	 * <div>
	 * <p>creer(ok) : délégation gateway.creer + OutputDTO + message MESSAGE_CREER_OK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(ok) : délégation gateway.creer + OutputDTO + message MESSAGE_CREER_OK")
	public void testCreerOk() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		/* pas doublon => findByLibelle retourne null. */
		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(null);

		/* parent persistant requis par le contrat du CU. */
		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);
		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parent);

		final SousTypeProduit cree = new SousTypeProduit(OUTILLAGE, parent);
		cree.setIdSousTypeProduit(1L);

		when(gateway.creer(any(SousTypeProduit.class))).thenReturn(cree);

		// ===================== ACT =====================

		final OutputDTO retour = service.creer(dto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNotNull();
		assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_OK);

		final ArgumentCaptor<SousTypeProduit> captor = ArgumentCaptor.forClass(SousTypeProduit.class);
		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).creer(captor.capture());

		final SousTypeProduit envoye = captor.getValue();
		assertThat(envoye).isNotNull();
		assertThat(envoye.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(envoye.getTypeProduit()).isNotNull();
		assertThat(envoye.getTypeProduit().getTypeProduit()).isEqualTo(BAZAR);
	}

	/**
	 * <div>
	 * <p>creer(dto) : parent TypeProduit absent en stockage -> IllegalStateException + message MESSAGE_PAS_PARENT.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(dto) : parent absent -> IllegalStateException + message MESSAGE_PAS_PARENT")
	public void testCreerPasParent() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		/* pas doublon => findByLibelle retourne null. */
		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(null);

		/* parent absent => typeProduitGateway retourne null. */
		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(null);

		// ===================== ACT =====================

		assertThatThrownBy(() -> service.creer(dto))
			.isInstanceOf(IllegalStateException.class);

		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, never()).creer(any(SousTypeProduit.class));
	}

	// ========================= TESTS rechercherTous() ====================

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

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.rechercherTous()).thenReturn(null);

		// ===================== ACT =====================

		assertThatThrownBy(() -> service.rechercherTous())
			.isInstanceOf(ExceptionStockageVide.class);

		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);
		verify(gateway, times(1)).rechercherTous();
	}

	/**
	 * <div>
	 * <p>rechercherTous() : liste vide -> retourne liste vide + message MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTous() : liste vide -> liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testRechercherTousVide() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.rechercherTous()).thenReturn(new ArrayList<SousTypeProduit>());

		// ===================== ACT =====================

		final List<OutputDTO> retour = service.rechercherTous();
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNotNull().isEmpty();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		verify(gateway, times(1)).rechercherTous();
	}

	
	
	// ===================== TESTS rechercherTousString() ==================

	
	
	/**
	 * <div>
	 * <p>rechercherTousString() : OK.</p>
	 * </div>
	 *
	 * @throws Exception si une erreur survient
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousString(ok) : liste non vide + message MESSAGE_RECHERCHE_OK")
	public void testRechercherTousStringOk() throws Exception {

	    // ===================== ARRANGE =====================

	    final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
	    final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
	    final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

	    final TypeProduit parent = new TypeProduit(BAZAR);
	    parent.setIdTypeProduit(1L);

	    final SousTypeProduit stp1 = new SousTypeProduit(OUTILLAGE, parent);
	    stp1.setIdSousTypeProduit(1L);

	    final SousTypeProduit stp2 = new SousTypeProduit(VETEMENT, parent);
	    stp2.setIdSousTypeProduit(2L);

	    when(gateway.rechercherTous()).thenReturn(Arrays.asList(stp1, stp2));

	    // ===================== ACT =====================

	    final List<String> retour = service.rechercherTousString();
	    final String message = service.getMessage();

	    // ===================== ASSERT =====================

	    assertThat(retour).isNotNull().hasSize(2);
	    assertThat(retour).contains(OUTILLAGE, VETEMENT);
	    assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

	    verify(gateway, times(1)).rechercherTous();
	    verifyNoInteractions(typeProduitGateway);
	}
	
	
	
	/**
	 * <div>
	 * <p>rechercherTousString() : stockage null -> ExceptionStockageVide + message MESSAGE_STOCKAGE_NULL.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousString() : gateway retourne null -> ExceptionStockageVide + message MESSAGE_STOCKAGE_NULL")
	public void testRechercherTousStringStockageNull() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.rechercherTous()).thenReturn(null);

		// ===================== ACT =====================

		assertThatThrownBy(() -> service.rechercherTousString())
			.isInstanceOf(ExceptionStockageVide.class);

		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);
		verify(gateway, times(1)).rechercherTous();
	}

	/**
	 * <div>
	 * <p>rechercherTousString() : liste vide -> retourne liste vide + message MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousString() : liste vide -> liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testRechercherTousStringVide() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.rechercherTous()).thenReturn(new ArrayList<SousTypeProduit>());

		// ===================== ACT =====================

		final List<String> retour = service.rechercherTousString();
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNotNull().isEmpty();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		verify(gateway, times(1)).rechercherTous();
	}

	/**
	 * <div>
	 * <p>rechercherTousString() : libellés blank ignorés -> liste vide + message MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousString() : libellés blank ignorés -> liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testRechercherTousStringLibellesBlank() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final TypeProduit parent = new TypeProduit(BAZAR);

		final SousTypeProduit stpBlank = new SousTypeProduit(ESPACES, parent);
		final SousTypeProduit stpNull = new SousTypeProduit(null, parent);

		when(gateway.rechercherTous()).thenReturn(Arrays.asList(stpBlank, stpNull));

		// ===================== ACT =====================

		final List<String> retour = service.rechercherTousString();
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNotNull().isEmpty();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		verify(gateway, times(1)).rechercherTous();
	}

	// ================== TESTS rechercherTousParPage(...) =================

	/**
	 * <div>
	 * <p>rechercherTousParPage(null) : IllegalStateException + message MESSAGE_PAGEABLE_NULL.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousParPage(null) : IllegalStateException + message MESSAGE_PAGEABLE_NULL")
	public void testRechercherTousParPageNull() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		// ===================== ACT =====================

		assertThatThrownBy(() -> service.rechercherTousParPage(null))
			.isInstanceOf(IllegalStateException.class);

		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_PAGEABLE_NULL);
		verifyNoInteractions(gateway);
	}

	/**
	 * <div>
	 * <p>rechercherTousParPage(page) : gateway retourne null -> retourne null + message MESSAGE_RECHERCHE_PAGINEE_KO.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousParPage(gateway null) : retourne null + message MESSAGE_RECHERCHE_PAGINEE_KO")
	public void testRechercherTousParPageGatewayNull() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final RequetePage page = new RequetePage(0, 2);

		when(gateway.rechercherTousParPage(page)).thenReturn(null);

		// ===================== ACT =====================

		final ResultatPage<OutputDTO> retour = service.rechercherTousParPage(page);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_KO);
		verify(gateway, times(1)).rechercherTousParPage(page);
	}
	
	

	/**
	 * <div>
	 * <p>rechercherTousParPage(page) : OK.</p>
	 * </div>
	 *
	 * @throws Exception si une erreur survient
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousParPage(ok) : ResultatPage OutputDTO + message MESSAGE_RECHERCHE_PAGINEE_OK")
	public void testRechercherTousParPageOk() throws Exception {

	    // ===================== ARRANGE =====================

	    final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
	    final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
	    final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

	    final RequetePage page = new RequetePage(0, 2);

	    final TypeProduit parent = new TypeProduit(BAZAR);
	    parent.setIdTypeProduit(1L);

	    final SousTypeProduit stp1 = new SousTypeProduit(OUTILLAGE, parent);
	    stp1.setIdSousTypeProduit(1L);

	    final SousTypeProduit stp2 = new SousTypeProduit(VETEMENT, parent);
	    stp2.setIdSousTypeProduit(2L);

	    final ResultatPage<SousTypeProduit> rp = new ResultatPage<SousTypeProduit>(
	        Arrays.asList(stp1, stp2), 0, 2, 2L);

	    when(gateway.rechercherTousParPage(page)).thenReturn(rp);

	    // ===================== ACT =====================

	    final ResultatPage<OutputDTO> retour = service.rechercherTousParPage(page);
	    final String message = service.getMessage();

	    // ===================== ASSERT =====================

	    assertThat(retour).isNotNull();
	    assertThat(retour.getContent()).isNotNull().hasSize(2);
	    assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

	    verify(gateway, times(1)).rechercherTousParPage(page);
	    verifyNoInteractions(typeProduitGateway);
	}
	
	
	
	/**
	 * <div>
	 * <p>rechercherTousParPage(page) : contenu null dans le ResultatPage Gateway -> retourne une page vide + message MESSAGE_RECHERCHE_PAGINEE_OK.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousParPage(contenu null) : page vide + message MESSAGE_RECHERCHE_PAGINEE_OK")
	public void testRechercherTousParPageContenuNull() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final RequetePage page = new RequetePage(0, 2);

		final ResultatPage<SousTypeProduit> rpGateway = new ResultatPage<SousTypeProduit>(null, 0, 2, 10L);

		when(gateway.rechercherTousParPage(page)).thenReturn(rpGateway);

		// ===================== ACT =====================

		final ResultatPage<OutputDTO> retour = service.rechercherTousParPage(page);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNotNull();
		assertThat(retour.getContent()).isNotNull().isEmpty();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);
		verify(gateway, times(1)).rechercherTousParPage(page);
	}

	// ======================= TESTS findByLibelle(...) ====================

	/**
	 * <div>
	 * <p>findByLibelle(blank) : retourne null + message MESSAGE_PARAM_BLANK.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(blank) : retourne null + message MESSAGE_PARAM_BLANK")
	public void testFindByLibelleBlank() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		// ===================== ACT =====================

		final OutputDTO retour = service.findByLibelle(ESPACES);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
	}

	/**
	 * <div>
	 * <p>findByLibelle(introuvable) : retourne null + message MESSAGE_OBJ_INTROUVABLE + libellé.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(introuvable) : retourne null + message MESSAGE_OBJ_INTROUVABLE")
	public void testFindByLibelleIntrouvable() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(null);

		// ===================== ACT =====================

		final OutputDTO retour = service.findByLibelle(OUTILLAGE);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + OUTILLAGE);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
	}

	
	
	/**
	 * <div>
	 * <p>findByLibelle(ok) : retourne OutputDTO + message MESSAGE_SUCCES_RECHERCHE.</p>
	 * </div>
	 *
	 * @throws Exception si une erreur survient
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(ok) : OutputDTO + message MESSAGE_SUCCES_RECHERCHE")
	public void testFindByLibelleOk() throws Exception {

	    // ===================== ARRANGE =====================

	    final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
	    final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
	    final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

	    final TypeProduit parent = new TypeProduit(BAZAR);
	    parent.setIdTypeProduit(1L);

	    final SousTypeProduit stp = new SousTypeProduit(OUTILLAGE, parent);
	    stp.setIdSousTypeProduit(1L);

	    when(gateway.findByLibelle(OUTILLAGE).get(0)).thenReturn(stp);

	    // ===================== ACT =====================

	    final OutputDTO retour = service.findByLibelle(OUTILLAGE);
	    final String message = service.getMessage();

	    // ===================== ASSERT =====================

	    assertThat(retour).isNotNull();
	    assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
	    assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
	    assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

	    verify(gateway, times(1)).findByLibelle(OUTILLAGE);
	    verifyNoInteractions(typeProduitGateway);
	}
	
	
	
	// ===================== TESTS findByLibelleRapide(...) =================

	/**
	 * <div>
	 * <p>findByLibelleRapide(null) : IllegalStateException + message MESSAGE_PARAM_NULL.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(null) : IllegalStateException + message MESSAGE_PARAM_NULL")
	public void testFindByLibelleRapideNull() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		// ===================== ACT =====================

		assertThatThrownBy(() -> service.findByLibelleRapide(null))
			.isInstanceOf(IllegalStateException.class);

		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_NULL);
		verifyNoInteractions(gateway);
	}

	
	
	/**
	 * <div>
	 * <p>findByLibelleRapide(blank) : délègue à rechercherTous().</p>
	 * </div>
	 *
	 * @throws Exception si une erreur survient
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(blank) : délègue rechercherTous()")
	public void testFindByLibelleRapideBlankDelegueRechercherTous() throws Exception {

	    // ===================== ARRANGE =====================

	    final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
	    final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
	    final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

	    final TypeProduit parent = new TypeProduit(BAZAR);
	    parent.setIdTypeProduit(1L);

	    final SousTypeProduit stp1 = new SousTypeProduit(OUTILLAGE, parent);
	    stp1.setIdSousTypeProduit(1L);

	    when(gateway.rechercherTous()).thenReturn(Arrays.asList(stp1));

	    // ===================== ACT =====================

	    final List<OutputDTO> retour = service.findByLibelleRapide(ESPACES);
	    final String message = service.getMessage();

	    // ===================== ASSERT =====================

	    assertThat(retour).isNotNull().hasSize(1);
	    assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

	    verify(gateway, times(1)).rechercherTous();
	    verify(gateway, never()).findByLibelleRapide(any(String.class));
	    verifyNoInteractions(typeProduitGateway);
	}
	
	
	
	/**
	 * <div>
	 * <p>findByLibelleRapide(contenu) : gateway retourne null -> RuntimeException + message KO_TECHNIQUE_RECHERCHE.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(gateway null) : RuntimeException + message KO_TECHNIQUE_RECHERCHE")
	public void testFindByLibelleRapideGatewayNull() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.findByLibelleRapide(TOU)).thenReturn(null);

		// ===================== ACT =====================

		assertThatThrownBy(() -> service.findByLibelleRapide(TOU))
			.isInstanceOf(RuntimeException.class);

		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE);
		verify(gateway, times(1)).findByLibelleRapide(TOU);
	}

	/**
	 * <div>
	 * <p>findByLibelleRapide(contenu) : aucun résultat -> liste vide + message MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(vide) : liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testFindByLibelleRapideVide() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.findByLibelleRapide(TOU)).thenReturn(new ArrayList<SousTypeProduit>());

		// ===================== ACT =====================

		final List<OutputDTO> retour = service.findByLibelleRapide(TOU);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNotNull().isEmpty();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);
		verify(gateway, times(1)).findByLibelleRapide(TOU);
	}

	/**
	 * <div>
	 * <p>findByLibelleRapide(contenu) : résultats -> liste non vide + message MESSAGE_RECHERCHE_OK.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(ok) : liste non vide + message MESSAGE_RECHERCHE_OK")
	public void testFindByLibelleRapideOk() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final TypeProduit parent = new TypeProduit(TOURISME);

		final SousTypeProduit stp1 = new SousTypeProduit(TOURISME_A, parent);
		stp1.setIdSousTypeProduit(1L);

		final SousTypeProduit stp2 = new SousTypeProduit(TOURISME_B, parent);
		stp2.setIdSousTypeProduit(2L);

		when(gateway.findByLibelleRapide(TOU)).thenReturn(Arrays.asList(stp1, stp2));

		// ===================== ACT =====================

		final List<OutputDTO> retour = service.findByLibelleRapide(TOU);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNotNull().hasSize(2);
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);
		verify(gateway, times(1)).findByLibelleRapide(TOU);
	}

	// ========================== TESTS findByDTO(...) =====================

	/**
	 * <div>
	 * <p>findByDTO(null) : retourne null + message MESSAGE_RECHERCHE_OBJ_NULL.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(null) : retourne null + message MESSAGE_RECHERCHE_OBJ_NULL")
	public void testFindByDTONull() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		// ===================== ACT =====================

		final OutputDTO retour = service.findByDTO(null);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OBJ_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);
	}

	/**
	 * <div>
	 * <p>findByDTO(dto) : pas de parent -> IllegalStateException + message MESSAGE_PAS_PARENT.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(pas parent) : IllegalStateException + message MESSAGE_PAS_PARENT")
	public void testFindByDTOPasParent() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(ESPACES, OUTILLAGE);

		// ===================== ACT =====================

		assertThatThrownBy(() -> service.findByDTO(dto))
			.isInstanceOf(IllegalStateException.class);

		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);
	}

	/**
	 * <div>
	 * <p>findByDTO(dto) : parent absent/non persistant -> retourne null + message MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(parent absent) : retourne null + message MESSAGE_RECHERCHE_VIDE")
	public void testFindByDTOParentAbsent() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(null);

		// ===================== ACT =====================

		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);
	}

	/**
	 * <div>
	 * <p>findByDTO(dto) : possibles vides -> retourne null + message MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(possibles vides) : retourne null + message MESSAGE_RECHERCHE_VIDE")
	public void testFindByDTOPossiblesVides() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);
		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parent);

		when(gateway.findAllByParent(any(TypeProduit.class))).thenReturn(new ArrayList<SousTypeProduit>());

		// ===================== ACT =====================

		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
	}

	/**
	 * <div>
	 * <p>findByDTO(dto) : introuvable dans liste -> retourne null + message MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(introuvable liste) : retourne null + message MESSAGE_RECHERCHE_VIDE")
	public void testFindByDTOIntrouvableDansListe() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);
		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parent);

		final SousTypeProduit autre = new SousTypeProduit(VETEMENT, parent);
		autre.setIdSousTypeProduit(2L);

		when(gateway.findAllByParent(any(TypeProduit.class))).thenReturn(Arrays.asList(autre));

		// ===================== ACT =====================

		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
	}

	/**
	 * <div>
	 * <p>findByDTO(dto) : OK -> OutputDTO + message MESSAGE_SUCCES_RECHERCHE.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(ok) : OutputDTO + message MESSAGE_SUCCES_RECHERCHE")
	public void testFindByDTOOk() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);
		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parent);

		final SousTypeProduit stp = new SousTypeProduit(OUTILLAGE, parent);
		stp.setIdSousTypeProduit(1L);

		when(gateway.findAllByParent(any(TypeProduit.class))).thenReturn(Arrays.asList(stp));

		// ===================== ACT =====================

		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNotNull();
		assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
	}

	/**
	 * <div>
	 * <p>findByDTO(dto) : exception technique du Gateway -> RuntimeException + message KO_TECHNIQUE_RECHERCHE.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(erreur technique) : RuntimeException + message KO_TECHNIQUE_RECHERCHE")
	public void testFindByDTOErreurTechnique() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);
		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parent);

		doThrow(new RuntimeException(MESSAGE_GATEWAY))
			.when(gateway).findAllByParent(any(TypeProduit.class));

		// ===================== ACT =====================

		assertThatThrownBy(() -> service.findByDTO(dto))
			.isInstanceOf(RuntimeException.class);

		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
	}

	// ===================== TESTS findAllByParent(...) ====================

	/**
	 * <div>
	 * <p>findAllByParent(dto) : parent absent ou non persistant -> IllegalStateException + message MESSAGE_PAS_PARENT.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(parent absent) : IllegalStateException + message MESSAGE_PAS_PARENT")
	public void testFindAllByParentPasParent() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final TypeProduitDTO.InputDTO parentDto = new TypeProduitDTO.InputDTO(BAZAR);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(null);

		// ===================== ACT =====================

		assertThatThrownBy(() -> service.findAllByParent(parentDto))
			.isInstanceOf(IllegalStateException.class);

		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, never()).findAllByParent(any(TypeProduit.class));
	}

	/**
	 * <div>
	 * <p>findAllByParent(null) : RuntimeException + message RECHERCHE_TYPEPRODUIT_NULL.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(null) : RuntimeException + message RECHERCHE_TYPEPRODUIT_NULL")
	public void testFindAllByParentNull() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		// ===================== ACT =====================

		assertThatThrownBy(() -> service.findAllByParent(null))
			.isInstanceOf(RuntimeException.class);

		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.RECHERCHE_TYPEPRODUIT_NULL);
		verifyNoInteractions(gateway);
	}

	/**
	 * <div>
	 * <p>findAllByParent(vide) : liste vide + message MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(vide) : liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testFindAllByParentVide() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final TypeProduitDTO.InputDTO parentDto = new TypeProduitDTO.InputDTO(BAZAR);
		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);
		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parent);

		when(gateway.findAllByParent(any(TypeProduit.class))).thenReturn(new ArrayList<SousTypeProduit>());

		// ===================== ACT =====================

		final List<OutputDTO> retour = service.findAllByParent(parentDto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNotNull().isEmpty();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
	}

	/**
	 * <div>
	 * <p>findAllByParent(ok) : liste non vide + message MESSAGE_RECHERCHE_OK.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(ok) : liste non vide + message MESSAGE_RECHERCHE_OK")
	public void testFindAllByParentOk() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final TypeProduitDTO.InputDTO parentDto = new TypeProduitDTO.InputDTO(BAZAR);
		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);
		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parent);

		final SousTypeProduit stp1 = new SousTypeProduit(OUTILLAGE, parent);
		stp1.setIdSousTypeProduit(1L);

		final SousTypeProduit stp2 = new SousTypeProduit(VETEMENT, parent);
		stp2.setIdSousTypeProduit(2L);

		when(gateway.findAllByParent(any(TypeProduit.class))).thenReturn(Arrays.asList(stp1, stp2));

		// ===================== ACT =====================

		final List<OutputDTO> retour = service.findAllByParent(parentDto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNotNull().hasSize(2);
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
	}

	// ======================== TESTS findById(...) ========================

	/**
	 * <div>
	 * <p>findById(null) : retourne null + message MESSAGE_PARAM_NULL.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(null) : retourne null + message MESSAGE_PARAM_NULL")
	public void testFindByIdNull() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		// ===================== ACT =====================

		final OutputDTO retour = service.findById(null);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);
	}

	/**
	 * <div>
	 * <p>findById(introuvable) : retourne null + message MESSAGE_OBJ_INTROUVABLE + id.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(introuvable) : retourne null + message MESSAGE_OBJ_INTROUVABLE")
	public void testFindByIdIntrouvable() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.findById(1L)).thenReturn(null);

		// ===================== ACT =====================

		final OutputDTO retour = service.findById(1L);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + 1L);

		verify(gateway, times(1)).findById(1L);
	}
	
	

	/**
	 * <div>
	 * <p>findById(ok) : OutputDTO + message MESSAGE_SUCCES_RECHERCHE.</p>
	 * </div>
	 *
	 * @throws Exception si une erreur survient
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(ok) : OutputDTO + message MESSAGE_SUCCES_RECHERCHE")
	public void testFindByIdOk() throws Exception {

	    // ===================== ARRANGE =====================

	    final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
	    final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
	    final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

	    final TypeProduit parent = new TypeProduit(BAZAR);
	    parent.setIdTypeProduit(1L);

	    final SousTypeProduit stp = new SousTypeProduit(OUTILLAGE, parent);
	    stp.setIdSousTypeProduit(1L);

	    when(gateway.findById(1L)).thenReturn(stp);

	    // ===================== ACT =====================

	    final OutputDTO retour = service.findById(1L);
	    final String message = service.getMessage();

	    // ===================== ASSERT =====================

	    assertThat(retour).isNotNull();
	    assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
	    assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
	    assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

	    verify(gateway, times(1)).findById(1L);
	    verifyNoInteractions(typeProduitGateway);
	}
	
	
	
	// ========================= TESTS update(...) =========================

	/**
	 * <div>
	 * <p>update(null) : ExceptionParametreNull + message MESSAGE_PARAM_NULL.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(null) : ExceptionParametreNull + message MESSAGE_PARAM_NULL")
	public void testUpdateNull() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		// ===================== ACT =====================

		assertThatThrownBy(() -> service.update(null))
			.isInstanceOf(ExceptionParametreNull.class);

		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_NULL);
		verifyNoInteractions(gateway);
	}

	/**
	 * <div>
	 * <p>update(blank) : ExceptionParametreBlank + message MESSAGE_PARAM_BLANK.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(blank) : ExceptionParametreBlank + message MESSAGE_PARAM_BLANK")
	public void testUpdateBlank() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, ESPACES);

		// ===================== ACT =====================

		assertThatThrownBy(() -> service.update(dto))
			.isInstanceOf(ExceptionParametreBlank.class);

		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);
		verifyNoInteractions(gateway);
	}

	/**
	 * <div>
	 * <p>update(introuvable) : retourne null + message MESSAGE_OBJ_INTROUVABLE + libellé.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(introuvable) : retourne null + message MESSAGE_OBJ_INTROUVABLE")
	public void testUpdateIntrouvable() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(null);

		// ===================== ACT =====================

		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + OUTILLAGE);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).update(any(SousTypeProduit.class));
	}

	/**
	 * <div>
	 * <p>update(non persistant) : ExceptionNonPersistant + message MESSAGE_OBJ_NON_PERSISTE + libellé.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(non persistant) : ExceptionNonPersistant + message MESSAGE_OBJ_NON_PERSISTE")
	public void testUpdateNonPersistant() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, new TypeProduit(BAZAR));
		/* ID null => non persistant. */
		existant.setIdSousTypeProduit(null);

		when(gateway.findByLibelle(OUTILLAGE).get(0)).thenReturn(existant);

		// ===================== ACT =====================

		assertThatThrownBy(() -> service.update(dto))
			.isInstanceOf(ExceptionNonPersistant.class);

		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE + OUTILLAGE);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).update(any(SousTypeProduit.class));
	}

	/**
	 * <div>
	 * <p>update(gateway null) : retourne null + message MESSAGE_MODIF_KO + libellé.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(gateway null) : retourne null + message MESSAGE_MODIF_KO")
	public void testUpdateGatewayNull() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, new TypeProduit(BAZAR));
		existant.setIdSousTypeProduit(1L);

		when(gateway.findByLibelle(OUTILLAGE).get(0)).thenReturn(existant);
		when(gateway.update(any(SousTypeProduit.class))).thenReturn(null);

		// ===================== ACT =====================

		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_MODIF_KO + OUTILLAGE);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).update(any(SousTypeProduit.class));
	}

	/**
	 * <div>
	 * <p>update(ok) : OutputDTO + message MESSAGE_MODIF_OK + typeProduit.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(ok) : OutputDTO + message MESSAGE_MODIF_OK")
	public void testUpdateOk() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, new TypeProduit(BAZAR));
		existant.setIdSousTypeProduit(1L);

		when(gateway.findByLibelle(OUTILLAGE).get(0)).thenReturn(existant);

		final SousTypeProduit modifie = new SousTypeProduit(OUTILLAGE.toUpperCase(Locale.getDefault()), new TypeProduit(BAZAR));
		modifie.setIdSousTypeProduit(1L);

		when(gateway.update(any(SousTypeProduit.class))).thenReturn(modifie);

		// ===================== ACT =====================

		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(retour).isNotNull();
		assertThat(retour.getSousTypeProduit()).isNotBlank();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_MODIF_OK + BAZAR);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).update(any(SousTypeProduit.class));
	}

	// ========================= TESTS delete(...) =========================

	/**
	 * <div>
	 * <p>delete(null) : violation de contrat -> ExceptionParametreNull + message MESSAGE_PARAM_NULL.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(null) : ExceptionParametreNull + message MESSAGE_PARAM_NULL")
	public void testDeleteNull() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		// ===================== ACT =====================

		assertThatThrownBy(() -> service.delete(null))
			.isInstanceOf(ExceptionParametreNull.class);

		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_NULL);
		verifyNoInteractions(gateway);
	}

	/**
	 * <div>
	 * <p>delete(blank) : violation de contrat -> ExceptionParametreBlank + message MESSAGE_PARAM_BLANK.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(blank) : ExceptionParametreBlank + message MESSAGE_PARAM_BLANK")
	public void testDeleteBlank() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, ESPACES);

		// ===================== ACT =====================

		assertThatThrownBy(() -> service.delete(dto))
			.isInstanceOf(ExceptionParametreBlank.class);

		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);
		verifyNoInteractions(gateway);
	}

	/**
	 * <div>
	 * <p>delete(introuvable) : ne supprime rien + message MESSAGE_OBJ_INTROUVABLE + libellé.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(introuvable) : ne supprime rien + message MESSAGE_OBJ_INTROUVABLE")
	public void testDeleteIntrouvable() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(null);

		// ===================== ACT =====================

		service.delete(dto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + OUTILLAGE);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).delete(any(SousTypeProduit.class));
	}

	/**
	 * <div>
	 * <p>delete(ok) : délègue gateway.delete + message MESSAGE_DELETE_OK + libellé.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(ok) : délègue gateway.delete + message MESSAGE_DELETE_OK")
	public void testDeleteOk() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, new TypeProduit(BAZAR));
		existant.setIdSousTypeProduit(1L);

		when(gateway.findByLibelle(OUTILLAGE).get(0)).thenReturn(existant);

		// ===================== ACT =====================

		service.delete(dto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_DELETE_OK + OUTILLAGE);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).delete(existant);
	}

	/**
	 * <div>
	 * <p>delete(echec) : gateway.delete jette exception -> Exception + message MESSAGE_DELETE_KO + libellé.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(echec) : Exception + message MESSAGE_DELETE_KO")
	public void testDeleteEchec() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, new TypeProduit(BAZAR));
		existant.setIdSousTypeProduit(1L);

		when(gateway.findByLibelle(OUTILLAGE).get(0)).thenReturn(existant);

		doThrow(new RuntimeException(MESSAGE_GATEWAY_BIS))
			.when(gateway).delete(existant);

		// ===================== ACT =====================

		assertThatThrownBy(() -> service.delete(dto))
			.isInstanceOf(RuntimeException.class);

		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_DELETE_KO + OUTILLAGE);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).delete(existant);
	}

	// ============================ TESTS count() ==========================

	/**
	 * <div>
	 * <p>count() : délègue gateway.count.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count() : délègue gateway.count()")
	public void testCount() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.count()).thenReturn(2L);

		// ===================== ACT =====================

		final long retour = service.count();

		// ===================== ASSERT =====================

		assertThat(retour).isEqualTo(2L);
		verify(gateway, times(1)).count();
	}

	// ========================= TESTS getMessage() ========================

	/**
	 * <div>
	 * <p>getMessage() : retourne le message thread-local.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("getMessage() : retourne le message courant")
	public void testGetMessage() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		// ===================== ACT =====================

		service.findByLibelle(ESPACES);
		final String message = service.getMessage();

		// ===================== ASSERT =====================

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);
	}

}
