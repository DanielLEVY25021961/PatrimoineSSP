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
	
	/**
	 * "IT_STP_GAMMA"
	 */
	public static final String IT_STP_GAMMA = "IT_STP_GAMMA";
	
	/**
	 * "IT_STP_DELTA"
	 */
	public static final String IT_STP_DELTA = "IT_STP_DELTA";
	

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
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link SousTypeProduitICuService#MESSAGE_CREER_NULL}</li>
	 * <li>n'interagit ni avec le gateway SousTypeProduit
	 * ni avec le gateway TypeProduit</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(null) : retourne null, message utilisateur, aucune interaction gateway")
	public void testCreerNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		/* ======================= ACT ======================= */
		final OutputDTO retour = service.creer(null);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>creer(blank) : violation de contrat applicatif.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_NOM_BLANK}</li>
	 * <li>n'interagit ni avec le gateway SousTypeProduit
	 * ni avec le gateway TypeProduit</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(blank) : ExceptionParametreBlank + message exact + aucune interaction gateway")
	public void testCreerBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, ESPACES);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
			.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(service.getMessage())
			.isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_NOM_BLANK);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>creer(controle technique KO avec message) :</p>
	 * <ul>
	 * <li>l'exception technique du contrôle d'unicité est propagée</li>
	 * <li>le message utilisateur est rationalisé avec
	 * {@link SousTypeProduitICuService#PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER}</li>
	 * <li>aucune création n'est tentée</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(controle technique KO avec message) : propage l'exception et rationalise le message utilisateur")
	public void testCreerControleTechniqueKoAvecMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final IllegalStateException panneTechnique
			= new IllegalStateException(MESSAGE_GATEWAY);

		when(gateway.findByLibelle(OUTILLAGE)).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				SousTypeProduitICuService.PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER
					+ MESSAGE_GATEWAY);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).creer(any(SousTypeProduit.class));
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>creer(controle technique KO sans message) :</p>
	 * <ul>
	 * <li>l'exception technique du contrôle d'unicité est propagée</li>
	 * <li>le message utilisateur retombe sur
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>aucune création n'est tentée</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(controle technique KO sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testCreerControleTechniqueKoSansMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.findByLibelle(OUTILLAGE)).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				SousTypeProduitICuService.PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER
					+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).creer(any(SousTypeProduit.class));
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>creer(doublon) : refus métier d'unicité.</p>
	 * <ul>
	 * <li>lève {@link ExceptionDoublon}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_DOUBLON} + libellé</li>
	 * <li>ne délègue jamais {@code gateway.creer(...)}</li>
	 * <li>n'interroge jamais le parent</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(doublon) : ExceptionDoublon + message exact + aucune création gateway")
	public void testCreerDoublon() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, new TypeProduit(BAZAR));
		existant.setIdSousTypeProduit(1L);

		when(gateway.findByLibelle(OUTILLAGE))
			.thenReturn(Arrays.asList(existant));

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
			.isInstanceOf(ExceptionDoublon.class);

		assertThat(service.getMessage())
			.isEqualTo(SousTypeProduitICuService.MESSAGE_DOUBLON + OUTILLAGE);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).creer(any(SousTypeProduit.class));
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>creer(parent blank) : le libellé du parent est inutilisable.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'interagit ni avec le gateway SousTypeProduit
	 * ni avec le gateway TypeProduit</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(parent blank) : IllegalStateException + message exact MESSAGE_PAS_PARENT + aucune interaction gateway")
	public void testCreerParentBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(ESPACES, OUTILLAGE);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>creer(parent technique KO avec message) :</p>
	 * <ul>
	 * <li>l'exception de lecture du parent est propagée</li>
	 * <li>le message utilisateur est rationalisé avec
	 * {@link SousTypeProduitICuService#PREFIX_MESSAGE_PARENT_TECHNIQUE_CREER}</li>
	 * <li>aucune création n'est tentée</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(parent technique KO avec message) : propage l'exception et rationalise le message utilisateur")
	public void testCreerParentTechniqueKoAvecMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(gateway.findByLibelle(OUTILLAGE))
			.thenReturn(new ArrayList<SousTypeProduit>());

		final IllegalStateException panneTechnique
			= new IllegalStateException(MESSAGE_GATEWAY);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				SousTypeProduitICuService.PREFIX_MESSAGE_PARENT_TECHNIQUE_CREER
					+ MESSAGE_GATEWAY);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, never()).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>creer(parent technique KO sans message) :</p>
	 * <ul>
	 * <li>l'exception de lecture du parent est propagée</li>
	 * <li>le message utilisateur retombe sur
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>aucune création n'est tentée</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(parent technique KO sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testCreerParentTechniqueKoSansMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(gateway.findByLibelle(OUTILLAGE))
			.thenReturn(new ArrayList<SousTypeProduit>());

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(typeProduitGateway.findByLibelle(BAZAR)).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				SousTypeProduitICuService.PREFIX_MESSAGE_PARENT_TECHNIQUE_CREER
					+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, never()).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>creer(parent absent) : aucun TypeProduit persistant n'est trouvé.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>ne délègue jamais la création</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(parent absent) : IllegalStateException + message exact MESSAGE_PAS_PARENT")
	public void testCreerPasParent() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(gateway.findByLibelle(OUTILLAGE))
			.thenReturn(new ArrayList<SousTypeProduit>());

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(null);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
			.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
			.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, never()).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>creer(parent non persistant) : le parent existe mais sans identifiant.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>ne délègue jamais la création</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(parent non persistant) : IllegalStateException + message exact MESSAGE_PAS_PARENT")
	public void testCreerParentNonPersistant() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(gateway.findByLibelle(OUTILLAGE))
			.thenReturn(new ArrayList<SousTypeProduit>());

		final TypeProduit parentNonPersistant = new TypeProduit(BAZAR);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentNonPersistant);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
			.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
			.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, never()).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>creer(création technique KO avec message) :</p>
	 * <ul>
	 * <li>l'exception du gateway est propagée</li>
	 * <li>le message utilisateur est rationalisé avec
	 * {@link SousTypeProduitICuService#PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(creation technique KO avec message) : propage l'exception du gateway et rationalise le message")
	public void testCreerCreationTechniqueKoAvecMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(gateway.findByLibelle(OUTILLAGE))
			.thenReturn(new ArrayList<SousTypeProduit>());

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);

		final IllegalStateException panneTechnique
			= new IllegalStateException(MESSAGE_GATEWAY);

		when(gateway.creer(any(SousTypeProduit.class))).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				SousTypeProduitICuService.PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER
					+ MESSAGE_GATEWAY);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>creer(création technique KO sans message) :</p>
	 * <ul>
	 * <li>l'exception du gateway est propagée</li>
	 * <li>le message utilisateur retombe sur
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(creation technique KO sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testCreerCreationTechniqueKoSansMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(gateway.findByLibelle(OUTILLAGE))
			.thenReturn(new ArrayList<SousTypeProduit>());

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.creer(any(SousTypeProduit.class))).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				SousTypeProduitICuService.PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER
					+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>creer(gateway retourne null) : aucun objet créé n'est renvoyé.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_CREATION_TECHNIQUE_KO_CREER}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(gateway retourne null) : IllegalStateException + message exact MESSAGE_CREATION_TECHNIQUE_KO_CREER")
	public void testCreerGatewayRetourneNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(gateway.findByLibelle(OUTILLAGE))
			.thenReturn(new ArrayList<SousTypeProduit>());

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.creer(any(SousTypeProduit.class))).thenReturn(null);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
			.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
			.isEqualTo(SousTypeProduitICuService.MESSAGE_CREATION_TECHNIQUE_KO_CREER);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>creer(conversion technique KO avec message) :</p>
	 * <ul>
	 * <li>l'exception de conversion est propagée</li>
	 * <li>le message utilisateur est rationalisé avec
	 * {@link SousTypeProduitICuService#PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(conversion technique KO avec message) : propage l'exception et rationalise le message utilisateur")
	public void testCreerConversionTechniqueKoAvecMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(gateway.findByLibelle(OUTILLAGE))
			.thenReturn(new ArrayList<SousTypeProduit>());

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);

		final SousTypeProduit cree = mock(SousTypeProduit.class);
		final IllegalStateException panneTechnique
			= new IllegalStateException(MESSAGE_GATEWAY_BIS);

		when(cree.getTypeProduit()).thenThrow(panneTechnique);
		when(gateway.creer(any(SousTypeProduit.class))).thenReturn(cree);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				SousTypeProduitICuService.PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER
					+ MESSAGE_GATEWAY_BIS);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>creer(conversion technique KO sans message) :</p>
	 * <ul>
	 * <li>l'exception de conversion est propagée</li>
	 * <li>le message utilisateur retombe sur
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(conversion technique KO sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testCreerConversionTechniqueKoSansMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(gateway.findByLibelle(OUTILLAGE))
			.thenReturn(new ArrayList<SousTypeProduit>());

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);

		final SousTypeProduit cree = mock(SousTypeProduit.class);
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(cree.getTypeProduit()).thenThrow(panneTechnique);
		when(gateway.creer(any(SousTypeProduit.class))).thenReturn(cree);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				SousTypeProduitICuService.PREFIX_MESSAGE_CONVERSION_TECHNIQUE_CREER
					+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).creer(any(SousTypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>creer(ok) : scénario nominal complet.</p>
	 * <ul>
	 * <li>cherche d'abord l'absence de doublon</li>
	 * <li>récupère ensuite le parent persistant</li>
	 * <li>délègue enfin {@code gateway.creer(...)}</li>
	 * <li>retourne un {@link OutputDTO} cohérent</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_CREER_OK}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(ok) : OutputDTO cohérent + parent persistant transmis + message exact MESSAGE_CREER_OK")
	public void testCreerOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(gateway.findByLibelle(OUTILLAGE))
			.thenReturn(new ArrayList<SousTypeProduit>());

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(1L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);

		final SousTypeProduit cree = new SousTypeProduit(OUTILLAGE, parentPersistant);
		cree.setIdSousTypeProduit(1L);

		when(gateway.creer(any(SousTypeProduit.class))).thenReturn(cree);

		/* ======================= ACT ======================= */
		final OutputDTO retour = service.creer(dto);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdSousTypeProduit()).isEqualTo(1L);
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
		assertThat(envoye.getTypeProduit().getIdTypeProduit()).isEqualTo(1L);

	} // __________________________________________________________________
	
	
	
	// ========================= TESTS rechercherTous() ====================

	
	
	/**
	 * <div>
	 * <p>rechercherTous() : stockage null.</p>
	 * <ul>
	 * <li>délègue une seule fois à {@code gateway.rechercherTous()}</li>
	 * <li>lève {@link ExceptionStockageVide}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_STOCKAGE_NULL}</li>
	 * <li>n'interagit jamais avec le gateway TypeProduit</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTous() : gateway retourne null -> ExceptionStockageVide + message MESSAGE_STOCKAGE_NULL")
	public void testRechercherTousStockageNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.rechercherTous()).thenReturn(null);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTous())
			.isInstanceOf(ExceptionStockageVide.class);

		assertThat(service.getMessage())
			.isEqualTo(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>rechercherTous() : recherche technique KO avec message.</p>
	 * <ul>
	 * <li>propage exactement l'exception du gateway</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + {@link SousTypeProduitICuService#TIRET_ESPACE}
	 * + message gateway</li>
	 * <li>n'interagit jamais avec le gateway TypeProduit</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTous() : gateway KO avec message -> propage l'exception + message technique rationalisé")
	public void testRechercherTousKoTechniqueAvecMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final IllegalStateException panneTechnique
			= new IllegalStateException(MESSAGE_GATEWAY);

		when(gateway.rechercherTous()).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTous())
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
					+ SousTypeProduitICuService.TIRET_ESPACE
					+ MESSAGE_GATEWAY);

		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>rechercherTous() : recherche technique KO sans message.</p>
	 * <ul>
	 * <li>propage exactement l'exception du gateway</li>
	 * <li>retombe sur
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>n'interagit jamais avec le gateway TypeProduit</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTous() : gateway KO sans message -> fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testRechercherTousKoTechniqueSansMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.rechercherTous()).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTous())
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
					+ SousTypeProduitICuService.TIRET_ESPACE
					+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>rechercherTous() : résultats vides après filtrage.</p>
	 * <ul>
	 * <li>le gateway retourne uniquement des éléments {@code null}</li>
	 * <li>retourne une liste vide mais non {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>n'interagit jamais avec le gateway TypeProduit</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTous() : résultats vides après filtrage -> liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testRechercherTousVideApresFiltrage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final List<SousTypeProduit> records = Arrays.asList(null, null);

		when(gateway.rechercherTous()).thenReturn(records);

		/* ======================= ACT ======================= */
		final List<OutputDTO> retour = service.rechercherTous();
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull().isEmpty();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>rechercherTous() : scénario nominal complet.</p>
	 * <ul>
	 * <li>retire les éléments {@code null}</li>
	 * <li>trie les objets métier</li>
	 * <li>dédoublonne les {@link OutputDTO}</li>
	 * <li>retourne une liste cohérente</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTous() : filtre nulls + trie + dédoublonne + message MESSAGE_RECHERCHE_OK")
	public void testRechercherTousOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final SousTypeProduit stpVetement = new SousTypeProduit(VETEMENT, new TypeProduit(BAZAR));
		final SousTypeProduit stpOutillage = new SousTypeProduit(OUTILLAGE, new TypeProduit(BAZAR));
		final SousTypeProduit stpOutillageDoublon = new SousTypeProduit(OUTILLAGE, new TypeProduit(BAZAR));

		when(gateway.rechercherTous())
			.thenReturn(Arrays.asList(stpVetement, null, stpOutillage, stpOutillageDoublon));

		/* ======================= ACT ======================= */
		final List<OutputDTO> retour = service.rechercherTous();
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);

		assertThat(retour.get(0).getTypeProduit()).isEqualTo(BAZAR);
		assertThat(retour.get(0).getSousTypeProduit()).isEqualTo(OUTILLAGE);

		assertThat(retour.get(1).getTypeProduit()).isEqualTo(BAZAR);
		assertThat(retour.get(1).getSousTypeProduit()).isEqualTo(VETEMENT);

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________

	
	
	// ===================== TESTS rechercherTousString() ==================

	
	
	/**
	 * <div>
	 * <p>rechercherTousString() : stockage null.</p>
	 * <ul>
	 * <li>délègue une seule fois à {@code gateway.rechercherTous()}</li>
	 * <li>lève {@link ExceptionStockageVide}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_STOCKAGE_NULL}</li>
	 * <li>n'interagit jamais avec le gateway TypeProduit</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousString() : gateway retourne null -> ExceptionStockageVide + message MESSAGE_STOCKAGE_NULL")
	public void testRechercherTousStringStockageNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.rechercherTous()).thenReturn(null);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTousString())
			.isInstanceOf(ExceptionStockageVide.class);

		assertThat(service.getMessage())
			.isEqualTo(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousString() : recherche technique KO avec message.</p>
	 * <ul>
	 * <li>propage exactement l'exception du gateway</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + {@link SousTypeProduitICuService#TIRET_ESPACE}
	 * + message gateway</li>
	 * <li>n'interagit jamais avec le gateway TypeProduit</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousString() : gateway KO avec message -> propage l'exception + message technique rationalisé")
	public void testRechercherTousStringKoTechniqueAvecMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final IllegalStateException panneTechnique
			= new IllegalStateException(MESSAGE_GATEWAY);

		when(gateway.rechercherTous()).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTousString())
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
					+ SousTypeProduitICuService.TIRET_ESPACE
					+ MESSAGE_GATEWAY);

		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousString() : recherche technique KO sans message.</p>
	 * <ul>
	 * <li>propage exactement l'exception du gateway</li>
	 * <li>retombe sur
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>n'interagit jamais avec le gateway TypeProduit</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousString() : gateway KO sans message -> fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testRechercherTousStringKoTechniqueSansMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.rechercherTous()).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTousString())
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
					+ SousTypeProduitICuService.TIRET_ESPACE
					+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousString() : résultats vides après filtrage.</p>
	 * <ul>
	 * <li>le gateway retourne uniquement des éléments {@code null}</li>
	 * <li>retourne une liste vide mais non {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>n'interagit jamais avec le gateway TypeProduit</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousString() : résultats vides après filtrage -> liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testRechercherTousStringVideApresFiltrage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final List<SousTypeProduit> records = Arrays.asList(null, null);

		when(gateway.rechercherTous()).thenReturn(records);

		/* ======================= ACT ======================= */
		final List<String> retour = service.rechercherTousString();
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull().isEmpty();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousString() : scénario nominal complet.</p>
	 * <ul>
	 * <li>retire les éléments {@code null}</li>
	 * <li>trie les objets métier</li>
	 * <li>ignore les libellés blank</li>
	 * <li>dédoublonne les libellés</li>
	 * <li>retourne une liste cohérente</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousString() : filtre nulls + trie + ignore blank + dédoublonne + message MESSAGE_RECHERCHE_OK")
	public void testRechercherTousStringOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);

		final SousTypeProduit stpVetement = new SousTypeProduit(VETEMENT, parent);
		stpVetement.setIdSousTypeProduit(1L);

		final SousTypeProduit stpOutillage = new SousTypeProduit(OUTILLAGE, parent);
		stpOutillage.setIdSousTypeProduit(2L);

		final SousTypeProduit stpOutillageDoublon = new SousTypeProduit(OUTILLAGE, parent);
		stpOutillageDoublon.setIdSousTypeProduit(3L);

		final SousTypeProduit stpBlank = new SousTypeProduit(ESPACES, parent);
		stpBlank.setIdSousTypeProduit(4L);

		when(gateway.rechercherTous())
			.thenReturn(Arrays.asList(stpVetement, null, stpOutillage, stpOutillageDoublon, stpBlank));

		/* ======================= ACT ======================= */
		final List<String> retour = service.rechercherTousString();
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);
		assertThat(retour).containsExactly(OUTILLAGE, VETEMENT);

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________	
	
	

	// ================== TESTS rechercherTousParPage(...) =================

	
	
	/**
	 * <div>
	 * <p>rechercherTousParPage(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAGEABLE_NULL}</li>
	 * <li>n'interagit jamais avec le gateway SousTypeProduit</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousParPage(null) : IllegalStateException + message MESSAGE_PAGEABLE_NULL")
	public void testRechercherTousParPageNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTousParPage(null))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage(SousTypeProduitICuService.MESSAGE_PAGEABLE_NULL);

		assertThat(service.getMessage())
			.isEqualTo(SousTypeProduitICuService.MESSAGE_PAGEABLE_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousParPage() : recherche technique KO avec message.</p>
	 * <ul>
	 * <li>propage exactement l'exception du gateway</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + {@link SousTypeProduitICuService#TIRET_ESPACE}
	 * + message gateway</li>
	 * <li>n'interagit jamais avec le gateway TypeProduit</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousParPage() : gateway KO avec message -> propage l'exception + message technique rationalisé")
	public void testRechercherTousParPageTechniqueKoAvecMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final RequetePage requete = new RequetePage(0, 2);

		final IllegalStateException panneTechnique
			= new IllegalStateException(MESSAGE_GATEWAY);

		when(gateway.rechercherTousParPage(requete)).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTousParPage(requete))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
					+ SousTypeProduitICuService.TIRET_ESPACE
					+ MESSAGE_GATEWAY);

		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousParPage() : recherche technique KO sans message.</p>
	 * <ul>
	 * <li>propage exactement l'exception du gateway</li>
	 * <li>retombe sur
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>n'interagit jamais avec le gateway TypeProduit</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousParPage() : gateway KO sans message -> fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testRechercherTousParPageTechniqueKoSansMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final RequetePage requete = new RequetePage(0, 2);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.rechercherTousParPage(requete)).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTousParPage(requete))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
					+ SousTypeProduitICuService.TIRET_ESPACE
					+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousParPage() : résultat paginé null.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_PAGINEE_KO}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousParPage() : gateway retourne null -> IllegalStateException + message MESSAGE_RECHERCHE_PAGINEE_KO")
	public void testRechercherTousParPageResultatNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final RequetePage requete = new RequetePage(0, 2);

		when(gateway.rechercherTousParPage(requete)).thenReturn(null);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTousParPage(requete))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage(SousTypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_KO);

		assertThat(service.getMessage())
			.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_KO);

		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousParPage() : scénario nominal complet.</p>
	 * <ul>
	 * <li>reprend le numéro de page</li>
	 * <li>reprend la taille de page</li>
	 * <li>reprend le total d'éléments</li>
	 * <li>filtre les {@code null}</li>
	 * <li>trie les objets métier</li>
	 * <li>dédoublonne les DTO</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_PAGINEE_OK}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousParPage() : pagination reprise + filtre nulls + trie + dédoublonne + message MESSAGE_RECHERCHE_PAGINEE_OK")
	public void testRechercherTousParPageOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final RequetePage requete = new RequetePage(0, 4);

		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);

		final SousTypeProduit stpVetement = new SousTypeProduit(VETEMENT, parent);
		stpVetement.setIdSousTypeProduit(2L);

		final SousTypeProduit stpOutillage = new SousTypeProduit(OUTILLAGE, parent);
		stpOutillage.setIdSousTypeProduit(1L);

		final SousTypeProduit stpOutillageDoublon = new SousTypeProduit(OUTILLAGE, parent);
		stpOutillageDoublon.setIdSousTypeProduit(1L);

		final ResultatPage<SousTypeProduit> resultatGateway
			= new ResultatPage<SousTypeProduit>(
					Arrays.asList(stpVetement, null, stpOutillage, stpOutillageDoublon),
					0,
					4,
					10L);

		when(gateway.rechercherTousParPage(requete)).thenReturn(resultatGateway);

		/* ======================= ACT ======================= */
		final ResultatPage<OutputDTO> retour = service.rechercherTousParPage(requete);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour.getPageNumber()).isEqualTo(0);
		assertThat(retour.getPageSize()).isEqualTo(4);
		assertThat(retour.getTotalElements()).isEqualTo(10L);

		assertThat(retour.getContent()).isNotNull();
		assertThat(retour.getContent()).hasSize(2);

		assertThat(retour.getContent())
			.extracting(OutputDTO::getSousTypeProduit)
			.containsExactly(OUTILLAGE, VETEMENT);

		assertThat(retour.getContent())
			.extracting(OutputDTO::getTypeProduit)
			.containsExactly(BAZAR, BAZAR);

		assertThat(retour.getContent())
			.extracting(OutputDTO::getIdSousTypeProduit)
			.containsExactly(1L, 2L);

		assertThat(message)
			.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________	
	

	
	// ======================= TESTS findByLibelle(...) ====================

	
	
	/**
	 * <div>
	 * <p>findByLibelle(blank) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne une liste vide mais non {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>n'interagit jamais avec le gateway SousTypeProduit</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(blank) : liste vide + message MESSAGE_PARAM_BLANK + aucune interaction gateway")
	public void testFindByLibelleBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		/* ======================= ACT ======================= */
		final List<OutputDTO> retour = service.findByLibelle(ESPACES);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull().isEmpty();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(stockage null) : anomalie technique.</p>
	 * <ul>
	 * <li>lève {@link ExceptionStockageVide}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_STOCKAGE_NULL}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(stockage null) : ExceptionStockageVide + message MESSAGE_STOCKAGE_NULL")
	public void testFindByLibelleStockageNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(null);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.findByLibelle(OUTILLAGE))
			.isInstanceOf(ExceptionStockageVide.class)
			.hasMessage(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
			.isEqualTo(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(KO technique avec message) : panne de lecture du gateway.</p>
	 * <ul>
	 * <li>propage exactement l'exception du gateway</li>
	 * <li>positionne le message utilisateur technique rationalisé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(KO technique avec message) : propage l'exception + message technique rationalisé")
	public void testFindByLibelleTechniqueKoAvecMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final IllegalStateException panneTechnique
			= new IllegalStateException(MESSAGE_GATEWAY);

		when(gateway.findByLibelle(OUTILLAGE)).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.findByLibelle(OUTILLAGE))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
					+ SousTypeProduitICuService.TIRET_ESPACE
					+ MESSAGE_GATEWAY);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(KO technique sans message) : fallback technique.</p>
	 * <ul>
	 * <li>propage exactement l'exception du gateway</li>
	 * <li>retombe sur
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testFindByLibelleTechniqueKoSansMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.findByLibelle(OUTILLAGE)).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.findByLibelle(OUTILLAGE))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
					+ SousTypeProduitICuService.TIRET_ESPACE
					+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(introuvable) : aucun résultat exact exploitable.</p>
	 * <ul>
	 * <li>retourne une liste vide mais non {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(introuvable) : liste vide + message MESSAGE_OBJ_INTROUVABLE + libellé")
	public void testFindByLibelleIntrouvable() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.findByLibelle(OUTILLAGE))
			.thenReturn(new ArrayList<SousTypeProduit>());

		/* ======================= ACT ======================= */
		final List<OutputDTO> retour = service.findByLibelle(OUTILLAGE);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull().isEmpty();
		assertThat(message)
			.isEqualTo(SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + OUTILLAGE);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(ok) : plusieurs résultats exacts possibles.</p>
	 * <ul>
	 * <li>retire les {@code null}</li>
	 * <li>trie les objets métier</li>
	 * <li>dédoublonne les DTO</li>
	 * <li>retourne une liste cohérente</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(ok) : liste DTO cohérente + plusieurs parents + message MESSAGE_SUCCES_RECHERCHE")
	public void testFindByLibelleOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final TypeProduit parentA = new TypeProduit(BAZAR);
		parentA.setIdTypeProduit(1L);

		final TypeProduit parentB = new TypeProduit(VETEMENT);
		parentB.setIdTypeProduit(2L);

		final SousTypeProduit stpA = new SousTypeProduit(OUTILLAGE, parentA);
		stpA.setIdSousTypeProduit(10L);

		final SousTypeProduit stpB = new SousTypeProduit(OUTILLAGE, parentB);
		stpB.setIdSousTypeProduit(20L);

		when(gateway.findByLibelle(OUTILLAGE))
			.thenReturn(Arrays.asList(stpB, null, stpA, stpA));

		/* ======================= ACT ======================= */
		final List<OutputDTO> retour = service.findByLibelle(OUTILLAGE);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);

		assertThat(retour)
			.extracting(OutputDTO::getSousTypeProduit)
			.containsExactly(OUTILLAGE, OUTILLAGE);

		assertThat(retour)
			.extracting(OutputDTO::getTypeProduit)
			.containsExactly(BAZAR, VETEMENT);

		assertThat(retour)
			.extracting(OutputDTO::getIdSousTypeProduit)
			.containsExactly(10L, 20L);

		assertThat(message)
			.isEqualTo(SousTypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________	

	
	
	// ===================== TESTS findByLibelleRapide(...) =================

	
	
	/**
	 * <div>
	 * <p>findByLibelleRapide(null) : émet MESSAGE_PARAM_NULL + LOG
	 * + IllegalStateException.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'interagit jamais avec le gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(null) : IllegalStateException + message MESSAGE_PARAM_NULL + aucune interaction gateway")
	public void testFindByLibelleRapideNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.findByLibelleRapide(null))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_PARAM_NULL);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Si pContenu est blank :</p>
	 * <ul>
	 * <li>délègue à {@code rechercherTous()} ;</li>
	 * <li>retourne la liste triée et dédoublonnée
	 * selon l'ordre métier observable ;</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(blank) : délègue à rechercherTous() + message MESSAGE_RECHERCHE_OK")
	public void testFindByLibelleRapideBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);

		final TypeProduit parentA = new TypeProduit(BAZAR);
		parentA.setIdTypeProduit(1L);

		final TypeProduit parentB = new TypeProduit(VETEMENT);
		parentB.setIdTypeProduit(2L);

		final SousTypeProduit stpGamma =
				new SousTypeProduit(IT_STP_GAMMA, parentA);
		stpGamma.setIdSousTypeProduit(1L);

		final SousTypeProduit stpDelta =
				new SousTypeProduit(IT_STP_DELTA, parentB);
		stpDelta.setIdSousTypeProduit(2L);

		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(stpDelta, null, stpGamma, stpDelta));

		/* ======================= ACT ======================= */
		final List<OutputDTO> retour = service.findByLibelleRapide(ESPACES);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);

		assertThat(retour)
				.extracting(OutputDTO::getSousTypeProduit)
				.containsExactly(IT_STP_GAMMA, IT_STP_DELTA);

		assertThat(retour)
				.extracting(OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, VETEMENT);

		assertThat(retour)
				.extracting(OutputDTO::getIdSousTypeProduit)
				.containsExactly(1L, 2L);

		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(gateway, times(1)).rechercherTous();
		verify(gateway, never()).findByLibelleRapide(any(String.class));
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>Si le gateway lève une exception avec message :
	 * propage l'exception et émet un message utilisateur technique
	 * rationalisé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(KO technique avec message) : propage l'exception + message technique rationalisé")
	public void testFindByLibelleRapideTechniqueKoAvecMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final String contenu = TOU;
		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);

		when(gateway.findByLibelleRapide(contenu)).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.findByLibelleRapide(contenu))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Si le gateway lève une exception sans message :
	 * propage l'exception et utilise le fallback
	 * MSG_ERREUR_NON_SPECIFIEE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testFindByLibelleRapideTechniqueKoSansMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final String contenu = TOU;
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.findByLibelleRapide(contenu)).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.findByLibelleRapide(contenu))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ SousTypeProduitICuService.TIRET_ESPACE
						+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Si le gateway retourne null :
	 * émet MESSAGE_STOCKAGE_NULL + LOG + ExceptionStockageVide.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(gateway retourne null) : ExceptionStockageVide + message MESSAGE_STOCKAGE_NULL")
	public void testFindByLibelleRapideStockageNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final String contenu = TOU;

		when(gateway.findByLibelleRapide(contenu)).thenReturn(null);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.findByLibelleRapide(contenu))
				.isInstanceOf(ExceptionStockageVide.class)
				.hasMessage(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Si la recherche retourne uniquement des valeurs non exploitables :
	 * retourne une liste vide + MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(vide après filtrage) : liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testFindByLibelleRapideVideApresFiltrage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final String contenu = TOU;
		final List<SousTypeProduit> records = new ArrayList<SousTypeProduit>();
		records.add(null);

		when(gateway.findByLibelleRapide(contenu)).thenReturn(records);

		/* ======================= ACT ======================= */
		final List<OutputDTO> retour = service.findByLibelleRapide(contenu);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Si la recherche retourne des objets exploitables :
	 * filtre les nulls, trie, dédoublonne,
	 * puis émet MESSAGE_RECHERCHE_OK.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(ok) : filtre nulls + trie + dédoublonne + message MESSAGE_RECHERCHE_OK")
	public void testFindByLibelleRapideOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final String contenu = TOU;

		final TypeProduit parentA = new TypeProduit(BAZAR);
		parentA.setIdTypeProduit(1L);

		final TypeProduit parentB = new TypeProduit(VETEMENT);
		parentB.setIdTypeProduit(2L);

		final SousTypeProduit stpA = new SousTypeProduit(TOURISME_A, parentA);
		stpA.setIdSousTypeProduit(1L);

		final SousTypeProduit stpB = new SousTypeProduit(TOURISME_B, parentB);
		stpB.setIdSousTypeProduit(2L);

		final SousTypeProduit stpADoublon = new SousTypeProduit(TOURISME_A, parentA);
		stpADoublon.setIdSousTypeProduit(1L);

		when(gateway.findByLibelleRapide(contenu))
				.thenReturn(Arrays.asList(stpB, null, stpA, stpADoublon));

		/* ======================= ACT ======================= */
		final List<OutputDTO> retour = service.findByLibelleRapide(contenu);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);

		assertThat(retour)
				.extracting(OutputDTO::getSousTypeProduit)
				.containsExactly(TOURISME_A, TOURISME_B);

		assertThat(retour)
				.extracting(OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, VETEMENT);

		assertThat(retour)
				.extracting(OutputDTO::getIdSousTypeProduit)
				.containsExactly(1L, 2L);

		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	
	
	// ===================== TESTS findAllByParent(...) ====================

	
	
	/**
	 * <div>
	 * <p>findAllByParent(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#RECHERCHE_TYPEPRODUIT_NULL}</li>
	 * <li>n'interagit jamais avec les gateways</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(null) : IllegalStateException + message RECHERCHE_TYPEPRODUIT_NULL")
	public void testFindAllByParentNull() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		// =================== ACT & ASSERT ==================

		assertThatThrownBy(() -> service.findAllByParent(null))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage(SousTypeProduitICuService.RECHERCHE_TYPEPRODUIT_NULL);

		assertThat(service.getMessage())
			.isEqualTo(SousTypeProduitICuService.RECHERCHE_TYPEPRODUIT_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(parent blank) : le parent transmis n'est pas exploitable.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'interagit jamais avec les gateways</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(parent blank) : IllegalStateException + message MESSAGE_PAS_PARENT")
	public void testFindAllByParentParentBlank() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final TypeProduitDTO.InputDTO parentDto = new TypeProduitDTO.InputDTO(ESPACES);

		// =================== ACT & ASSERT ==================

		assertThatThrownBy(() -> service.findAllByParent(parentDto))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(service.getMessage())
			.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(parent technique KO avec message) :
	 * propage l'exception du gateway parent
	 * et rationalise le message utilisateur.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(parent technique KO avec message) : propage l'exception + message technique rationalisé")
	public void testFindAllByParentParentTechniqueKoAvecMessage() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final TypeProduitDTO.InputDTO parentDto = new TypeProduitDTO.InputDTO(BAZAR);
		final IllegalStateException panneTechnique
			= new IllegalStateException(MESSAGE_GATEWAY);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenThrow(panneTechnique);

		// =================== ACT & ASSERT ==================

		assertThatThrownBy(() -> service.findAllByParent(parentDto))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
					+ SousTypeProduitICuService.TIRET_ESPACE
					+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(parent technique KO sans message) :
	 * utilise le fallback MSG_ERREUR_NON_SPECIFIEE.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(parent technique KO sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testFindAllByParentParentTechniqueKoSansMessage() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final TypeProduitDTO.InputDTO parentDto = new TypeProduitDTO.InputDTO(BAZAR);
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(typeProduitGateway.findByLibelle(BAZAR)).thenThrow(panneTechnique);

		// =================== ACT & ASSERT ==================

		assertThatThrownBy(() -> service.findAllByParent(parentDto))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
					+ SousTypeProduitICuService.TIRET_ESPACE
					+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(parent absent) : parent absent ou non persistant.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * </ul>
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

		// =================== ACT & ASSERT ==================

		assertThatThrownBy(() -> service.findAllByParent(parentDto))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(service.getMessage())
			.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, never()).findAllByParent(any(TypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(parent non persistant) : parent sans identifiant.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(parent non persistant) : IllegalStateException + message MESSAGE_PAS_PARENT")
	public void testFindAllByParentParentNonPersistant() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final TypeProduitDTO.InputDTO parentDto = new TypeProduitDTO.InputDTO(BAZAR);
		final TypeProduit parent = new TypeProduit(BAZAR);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parent);

		// =================== ACT & ASSERT ==================

		assertThatThrownBy(() -> service.findAllByParent(parentDto))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(service.getMessage())
			.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, never()).findAllByParent(any(TypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(enfants technique KO avec message) :
	 * propage l'exception du gateway enfants
	 * et rationalise le message utilisateur.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(enfants technique KO avec message) : propage l'exception + message technique rationalisé")
	public void testFindAllByParentEnfantsTechniqueKoAvecMessage() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final TypeProduitDTO.InputDTO parentDto = new TypeProduitDTO.InputDTO(BAZAR);
		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);

		final IllegalStateException panneTechnique
			= new IllegalStateException(MESSAGE_GATEWAY);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parent);
		when(gateway.findAllByParent(any(TypeProduit.class))).thenThrow(panneTechnique);

		// =================== ACT & ASSERT ==================

		assertThatThrownBy(() -> service.findAllByParent(parentDto))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
					+ SousTypeProduitICuService.TIRET_ESPACE
					+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(enfants technique KO sans message) :
	 * utilise le fallback MSG_ERREUR_NON_SPECIFIEE.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(enfants technique KO sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testFindAllByParentEnfantsTechniqueKoSansMessage() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final TypeProduitDTO.InputDTO parentDto = new TypeProduitDTO.InputDTO(BAZAR);
		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parent);
		when(gateway.findAllByParent(any(TypeProduit.class))).thenThrow(panneTechnique);

		// =================== ACT & ASSERT ==================

		assertThatThrownBy(() -> service.findAllByParent(parentDto))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
					+ SousTypeProduitICuService.TIRET_ESPACE
					+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(stockage null) : le gateway enfants retourne null.</p>
	 * <ul>
	 * <li>lève {@link ExceptionStockageVide}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_STOCKAGE_NULL}</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(stockage null) : ExceptionStockageVide + message MESSAGE_STOCKAGE_NULL")
	public void testFindAllByParentStockageNull() throws Exception {

		// ===================== ARRANGE =====================

		final SousTypeProduitGatewayIService gateway = mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway = mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service = new SousTypeProduitCuService(gateway, typeProduitGateway);

		final TypeProduitDTO.InputDTO parentDto = new TypeProduitDTO.InputDTO(BAZAR);
		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parent);
		when(gateway.findAllByParent(any(TypeProduit.class))).thenReturn(null);

		// =================== ACT & ASSERT ==================

		assertThatThrownBy(() -> service.findAllByParent(parentDto))
			.isInstanceOf(ExceptionStockageVide.class)
			.hasMessage(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
			.isEqualTo(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));

	} // __________________________________________________________________



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

		// ======================= ACT =======================

		final List<OutputDTO> retour = service.findAllByParent(parentDto);
		final String message = service.getMessage();

		// ===================== ASSERT ======================

		assertThat(retour).isNotNull().isEmpty();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(ok) : filtre les nulls, trie, dédoublonne
	 * et positionne MESSAGE_RECHERCHE_OK.</p>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(ok) : filtre nulls + trie + dédoublonne + message MESSAGE_RECHERCHE_OK")
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

		final SousTypeProduit stp1Doublon = new SousTypeProduit(OUTILLAGE, parent);
		stp1Doublon.setIdSousTypeProduit(1L);

		when(gateway.findAllByParent(any(TypeProduit.class)))
			.thenReturn(Arrays.asList(stp2, null, stp1, stp1Doublon));

		// ======================= ACT =======================

		final List<OutputDTO> retour = service.findAllByParent(parentDto);
		final String message = service.getMessage();

		// ===================== ASSERT ======================

		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);

		assertThat(retour)
			.extracting(OutputDTO::getSousTypeProduit)
			.containsExactly(OUTILLAGE, VETEMENT);

		assertThat(retour)
			.extracting(OutputDTO::getTypeProduit)
			.containsExactly(BAZAR, BAZAR);

		assertThat(retour)
			.extracting(OutputDTO::getIdSousTypeProduit)
			.containsExactly(1L, 2L);

		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));

	} // __________________________________________________________________	
	

	
	// ========================== TESTS findByDTO(...) =====================
	
	
	
	/**
	 * <div>
	 * <p>findByDTO(null) : 
	 * retourne null + message MESSAGE_RECHERCHE_OBJ_NULL.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(null) : retourne null + message MESSAGE_RECHERCHE_OBJ_NULL")
	public void testFindByDTONull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);

		/* ======================= ACT ======================= */
		final OutputDTO retour = service.findByDTO(null);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OBJ_NULL);
		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findByDTO(parent blank) : 
	 * IllegalStateException + message MESSAGE_PAS_PARENT.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(parent blank) : IllegalStateException + message MESSAGE_PAS_PARENT")
	public void testFindByDTOParentBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(ESPACES, OUTILLAGE);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);
		
	} // __________________________________________________________________
	

	
	/**
	 * <div>
	 * <p>findByDTO(parent KO technique avec message) :
	 * RuntimeException + message rationalisé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(parent KO technique avec message) : RuntimeException + message KO_TECHNIQUE_RECHERCHE + message")
	public void testFindByDTOErreurTechniqueRechercheParentAvecMessage()
			throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenThrow(new RuntimeException(MESSAGE_GATEWAY));

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isInstanceOf(RuntimeException.class)
				.hasMessage(MESSAGE_GATEWAY);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findByDTO(parent KO technique sans message) :
	 * RuntimeException + message rationalisé avec MSG_ERREUR_NON_SPECIFIEE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(parent KO technique sans message) : RuntimeException + message KO_TECHNIQUE_RECHERCHE + MSG_ERREUR_NON_SPECIFIEE")
	public void testFindByDTOErreurTechniqueRechercheParentSansMessage()
			throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenThrow(new RuntimeException());

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isInstanceOf(RuntimeException.class);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findByDTO(parent non persistant) :
	 * retourne null + message MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(parent non persistant) : retourne null + message MESSAGE_RECHERCHE_VIDE")
	public void testFindByDTOParentNonPersistant() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentNonPersistant = new TypeProduit(BAZAR);

		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentNonPersistant);

		/* ======================= ACT ======================= */
		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findByDTO(enfants KO technique avec message) :
	 * RuntimeException + message rationalisé.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(enfants KO technique avec message) : RuntimeException + message KO_TECHNIQUE_RECHERCHE + message")
	public void testFindByDTOErreurTechniqueRechercheEnfantsAvecMessage()
			throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parent);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenThrow(new RuntimeException(MESSAGE_GATEWAY));

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isInstanceOf(RuntimeException.class)
				.hasMessage(MESSAGE_GATEWAY);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		
	} // __________________________________________________________________
	

	
	/**
	 * <div>
	 * <p>findByDTO(enfants KO technique sans message) :
	 * RuntimeException + message rationalisé avec MSG_ERREUR_NON_SPECIFIEE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(enfants KO technique sans message) : RuntimeException + message KO_TECHNIQUE_RECHERCHE + MSG_ERREUR_NON_SPECIFIEE")
	public void testFindByDTOErreurTechniqueRechercheEnfantsSansMessage()
			throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parent);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenThrow(new RuntimeException());

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.findByDTO(dto))
				.isInstanceOf(RuntimeException.class);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		
	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findByDTO(vide) : retourne null + message MESSAGE_RECHERCHE_VIDE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(vide) : retourne null + message MESSAGE_RECHERCHE_VIDE")
	public void testFindByDTOVide() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parent);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(new ArrayList<>());

		/* ======================= ACT ======================= */
		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		
	} // __________________________________________________________________
	

	
	/**
	 * <div>
	 * <p>findByDTO(introuvable dans la liste) :
	 * retourne null + message MESSAGE_RECHERCHE_VIDE.</p>
	 *</div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(introuvable dans la liste) : retourne null + message MESSAGE_RECHERCHE_VIDE")
	public void testFindByDTOIntrouvableDansListe() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);

		final SousTypeProduit autre = new SousTypeProduit(VETEMENT, parent);
		autre.setIdSousTypeProduit(2L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parent);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(autre));

		/* ======================= ACT ======================= */
		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		
	} // __________________________________________________________________
	

	
	/**
	 * <div>
	 * <p>findByDTO(ok) : retourne le DTO du couple [parent, libellé]
	 * + message MESSAGE_SUCCES_RECHERCHE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(ok) : retourne le DTO du couple [parent, libellé] + message MESSAGE_SUCCES_RECHERCHE")
	public void testFindByDTOOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);

		final SousTypeProduit autre = new SousTypeProduit(VETEMENT, parent);
		autre.setIdSousTypeProduit(2L);

		final SousTypeProduit cible = new SousTypeProduit(OUTILLAGE, parent);
		cible.setIdSousTypeProduit(3L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parent);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(autre, null, cible));

		/* ======================= ACT ======================= */
		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(retour.getIdSousTypeProduit()).isEqualTo(3L);
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		
	} // __________________________________________________________________

		
	
	// ======================== TESTS findById(...) ========================

	
	
	/**
	 * <div>
	 * <p>findById(null) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'appelle pas le Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(null) : null + message MESSAGE_PARAM_NULL")
	public void testFindByIdNull() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);

		final OutputDTO retour = service.findById(null);
		final String message = service.getMessage();

		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findById(introuvable) : cas nominal de non-trouvabilité.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + id</li>
	 * <li>délègue une fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(introuvable) : null + message MESSAGE_OBJ_INTROUVABLE + id")
	public void testFindByIdIntrouvable() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final Long id = 12L;

		when(gateway.findById(id)).thenReturn(null);

		final OutputDTO retour = service.findById(id);
		final String message = service.getMessage();

		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + id);

		verify(gateway, times(1)).findById(id);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findById(KO technique avec message) : panne technique remontée
	 * par le Gateway.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>rationalise le message utilisateur</li>
	 * <li>délègue une fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(KO technique avec message) : RuntimeException + message rationalisé")
	public void testFindByIdErreurTechniqueAvecMessage() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final Long id = 13L;

		when(gateway.findById(id))
				.thenThrow(new RuntimeException(MESSAGE_GATEWAY));

		assertThatThrownBy(() -> service.findById(id))
				.isInstanceOf(RuntimeException.class)
				.hasMessage(MESSAGE_GATEWAY);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(gateway, times(1)).findById(id);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findById(KO technique sans message) : panne technique remontée
	 * par le Gateway.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>rationalise le message utilisateur avec
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>délègue une fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(KO technique sans message) : RuntimeException + message rationalisé")
	public void testFindByIdErreurTechniqueSansMessage() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final Long id = 14L;

		when(gateway.findById(id)).thenThrow(new RuntimeException());

		assertThatThrownBy(() -> service.findById(id))
				.isInstanceOf(RuntimeException.class);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findById(id);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findById(ok) : succès nominal de recherche par identifiant.</p>
	 * <ul>
	 * <li>retourne un OutputDTO cohérent</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}</li>
	 * <li>délègue une fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(ok) : retourne OutputDTO cohérent + message exact de succès")
	public void testFindByIdOk() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final Long id = 3L;

		final TypeProduit parent = new TypeProduit(BAZAR);
		parent.setIdTypeProduit(1L);

		final SousTypeProduit stp = new SousTypeProduit(OUTILLAGE, parent);
		stp.setIdSousTypeProduit(id);

		when(gateway.findById(id)).thenReturn(stp);

		final OutputDTO retour = service.findById(id);
		final String message = service.getMessage();

		assertThat(retour).isNotNull();
		assertThat(retour.getIdSousTypeProduit()).isEqualTo(id);
		assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		verify(gateway, times(1)).findById(id);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________	

	
	
	// ========================= TESTS update(...) =========================

	
	
	/**
	 * <div>
	 * <p>update(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'interagit ni avec le Gateway enfant
	 * ni avec le Gateway parent</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(null) : ExceptionParametreNull + message MESSAGE_PARAM_NULL + aucune interaction gateway")
	public void testUpdateNull() {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(null))
				.isInstanceOf(ExceptionParametreNull.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>n'interagit ni avec le Gateway enfant
	 * ni avec le Gateway parent</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(blank) : ExceptionParametreBlank + message MESSAGE_PARAM_BLANK + aucune interaction gateway")
	public void testUpdateBlank() {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, ESPACES);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(parent blank) : violation de contrat structurel.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'interagit ni avec le Gateway enfant
	 * ni avec le Gateway parent</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(parent blank) : IllegalStateException + message MESSAGE_PAS_PARENT + aucune interaction gateway")
	public void testUpdateParentBlank() {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(ESPACES, OUTILLAGE);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(recherche parent technique KO avec message) :
	 * panne technique pendant la recherche du parent persistant.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + détail technique</li>
	 * <li>n'appelle jamais le Gateway enfant</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(recherche parent KO technique avec message) : propage l'exception + message KO_TECHNIQUE_RECHERCHE")
	public void testUpdateRechercheParentTechniqueKoAvecMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);
		final IllegalStateException panneTechnique =
				new IllegalStateException(MESSAGE_GATEWAY);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenThrow(panneTechnique);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(recherche parent technique KO sans message) :
	 * panne technique sans message pendant
	 * la recherche du parent persistant.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>n'appelle jamais le Gateway enfant</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(recherche parent KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testUpdateRechercheParentTechniqueKoSansMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(typeProduitGateway.findByLibelle(BAZAR)).thenThrow(panneTechnique);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(parent absent) : le parent requis
	 * n'existe pas en stockage.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'appelle jamais le Gateway enfant</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(parent absent) : IllegalStateException + message MESSAGE_PAS_PARENT")
	public void testUpdateParentAbsent() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(null);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(parent non persistant) : le parent retrouvé
	 * existe mais ne porte pas d'identifiant persistant.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'appelle jamais le Gateway enfant</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(parent non persistant) : IllegalStateException + message MESSAGE_PAS_PARENT")
	public void testUpdateParentNonPersistant() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentNonPersistant = new TypeProduit(BAZAR);
		parentNonPersistant.setIdTypeProduit(null);

		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentNonPersistant);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(recherche enfants technique KO avec message) :
	 * panne technique pendant la recherche des enfants du parent.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + détail technique</li>
	 * <li>n'appelle jamais {@code gateway.update(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(recherche enfants KO technique avec message) : propage l'exception + message KO_TECHNIQUE_RECHERCHE")
	public void testUpdateRechercheEnfantsTechniqueKoAvecMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final IllegalStateException panneTechnique =
				new IllegalStateException(MESSAGE_GATEWAY);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenThrow(panneTechnique);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, never()).update(any(SousTypeProduit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(recherche enfants technique KO sans message) :
	 * panne technique sans message pendant
	 * la recherche des enfants du parent.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>n'appelle jamais {@code gateway.update(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(recherche enfants KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testUpdateRechercheEnfantsTechniqueKoSansMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenThrow(panneTechnique);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, never()).update(any(SousTypeProduit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(stockage null pendant ré-identification) :
	 * le Gateway retourne {@code null}
	 * pour les enfants du parent persistant.</p>
	 * <ul>
	 * <li>lève {@link ExceptionStockageVide}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_STOCKAGE_NULL}</li>
	 * <li>n'appelle jamais {@code gateway.update(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(stockage null) : ExceptionStockageVide + message MESSAGE_STOCKAGE_NULL")
	public void testUpdateStockageNullPendantReidentification() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class))).thenReturn(null);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionStockageVide.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, never()).update(any(SousTypeProduit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(introuvable) : aucun objet persistant
	 * ne correspond au couple [parent, libellé].</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}
	 * + libellé</li>
	 * <li>n'appelle jamais {@code gateway.update(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(introuvable) : null + message MESSAGE_OBJ_INTROUVABLE + libellé")
	public void testUpdateIntrouvable() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit autre = new SousTypeProduit(VETEMENT, parentPersistant);
		autre.setIdSousTypeProduit(22L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(autre, null));

		/* ======================= ACT ======================= */
		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE
								+ OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, never()).update(any(SousTypeProduit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(non persistant) : l'objet ré-identifié
	 * existe mais ne porte pas d'identifiant persistant.</p>
	 * <ul>
	 * <li>lève {@link ExceptionNonPersistant}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_NON_PERSISTE}
	 * + libellé</li>
	 * <li>n'appelle jamais {@code gateway.update(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(non persisté) : ExceptionNonPersistant + message MESSAGE_OBJ_NON_PERSISTE + libellé")
	public void testUpdateNonPersistant() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(null);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(existant));

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionNonPersistant.class);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE
								+ OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, never()).update(any(SousTypeProduit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(modification technique KO avec message) :
	 * le Gateway échoue pendant la délégation de modification.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_MODIF_KO}
	 * + libellé + tiret + détail technique</li>
	 * <li>prouve la réinjection de l'ID persistant
	 * et du parent persistant dans l'objet envoyé au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(modification KO technique avec message) : exception relancée + message MESSAGE_MODIF_KO + détail")
	public void testUpdateModificationTechniqueKoAvecMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(41L);

		final IllegalStateException panneTechnique =
				new IllegalStateException(MESSAGE_GATEWAY);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(existant));
		when(gateway.update(any(SousTypeProduit.class))).thenThrow(panneTechnique);

		final ArgumentCaptor<SousTypeProduit> captor =
				ArgumentCaptor.forClass(SousTypeProduit.class);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_MODIF_KO
								+ OUTILLAGE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, times(1)).update(captor.capture());

		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdSousTypeProduit()).isEqualTo(41L);
		assertThat(captor.getValue().getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(captor.getValue().getTypeProduit()).isNotNull();
		assertThat(captor.getValue().getTypeProduit().getTypeProduit()).isEqualTo(BAZAR);
		assertThat(captor.getValue().getTypeProduit().getIdTypeProduit()).isEqualTo(10L);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(modification technique KO sans message) :
	 * le Gateway échoue sans message
	 * pendant la délégation de modification.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>prouve la réinjection de l'ID persistant
	 * dans l'objet envoyé au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(modification KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testUpdateModificationTechniqueKoSansMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(42L);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(existant));
		when(gateway.update(any(SousTypeProduit.class))).thenThrow(panneTechnique);

		final ArgumentCaptor<SousTypeProduit> captor =
				ArgumentCaptor.forClass(SousTypeProduit.class);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_MODIF_KO
								+ OUTILLAGE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, times(1)).update(captor.capture());

		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdSousTypeProduit()).isEqualTo(42L);
		assertThat(captor.getValue().getTypeProduit()).isNotNull();
		assertThat(captor.getValue().getTypeProduit().getIdTypeProduit()).isEqualTo(10L);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(gateway null) : le Gateway retourne {@code null}
	 * après délégation de modification.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_MODIF_KO} + libellé</li>
	 * <li>prouve la réinjection de l'ID persistant
	 * dans l'objet envoyé au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(gateway null) : null + message MESSAGE_MODIF_KO + libellé")
	public void testUpdateGatewayNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(43L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(existant));
		when(gateway.update(any(SousTypeProduit.class))).thenReturn(null);

		final ArgumentCaptor<SousTypeProduit> captor =
				ArgumentCaptor.forClass(SousTypeProduit.class);

		/* ======================= ACT ======================= */
		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_MODIF_KO + OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, times(1)).update(captor.capture());

		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdSousTypeProduit()).isEqualTo(43L);
		assertThat(captor.getValue().getTypeProduit()).isNotNull();
		assertThat(captor.getValue().getTypeProduit().getIdTypeProduit()).isEqualTo(10L);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(retour non persistant) :
	 * le Gateway retourne un objet modifié
	 * dont l'identifiant est redevenu {@code null}.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_NON_PERSISTE}
	 * + libellé</li>
	 * <li>prouve la réinjection de l'ID persistant
	 * dans l'objet envoyé au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(retour non persistant) : IllegalStateException + message MESSAGE_OBJ_NON_PERSISTE + libellé")
	public void testUpdateRetourNonPersistant() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(44L);

		final SousTypeProduit modifie = new SousTypeProduit(OUTILLAGE, parentPersistant);
		modifie.setIdSousTypeProduit(null);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(existant));
		when(gateway.update(any(SousTypeProduit.class))).thenReturn(modifie);

		final ArgumentCaptor<SousTypeProduit> captor =
				ArgumentCaptor.forClass(SousTypeProduit.class);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE
								+ OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, times(1)).update(captor.capture());

		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdSousTypeProduit()).isEqualTo(44L);
		assertThat(captor.getValue().getTypeProduit()).isNotNull();
		assertThat(captor.getValue().getTypeProduit().getIdTypeProduit()).isEqualTo(10L);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>update(ok) : succès nominal complet.</p>
	 * <ul>
	 * <li>retourne un {@link OutputDTO} cohérent</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_MODIF_OK} + libellé</li>
	 * <li>prouve la réinjection de l'ID persistant
	 * et du parent persistant dans l'objet envoyé au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(ok) : OutputDTO cohérent + message MESSAGE_MODIF_OK + libellé + ID réinjecté")
	public void testUpdateOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(45L);

		final SousTypeProduit modifie = new SousTypeProduit(OUTILLAGE, parentPersistant);
		modifie.setIdSousTypeProduit(45L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(existant));
		when(gateway.update(any(SousTypeProduit.class))).thenReturn(modifie);

		final ArgumentCaptor<SousTypeProduit> captor =
				ArgumentCaptor.forClass(SousTypeProduit.class);

		/* ======================= ACT ======================= */
		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdSousTypeProduit()).isEqualTo(45L);
		assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(message)
				.isEqualTo(SousTypeProduitICuService.MESSAGE_MODIF_OK + OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, times(1)).update(captor.capture());

		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdSousTypeProduit()).isEqualTo(45L);
		assertThat(captor.getValue().getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(captor.getValue().getTypeProduit()).isNotNull();
		assertThat(captor.getValue().getTypeProduit().getTypeProduit()).isEqualTo(BAZAR);
		assertThat(captor.getValue().getTypeProduit().getIdTypeProduit()).isEqualTo(10L);

	} // __________________________________________________________________	
	

	
	// ========================= TESTS delete(...) =========================

	
	
	/**
	 * <div>
	 * <p>delete(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'interagit ni avec le Gateway enfant
	 * ni avec le Gateway parent</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(null) : ExceptionParametreNull + message MESSAGE_PARAM_NULL + aucune interaction gateway")
	public void testDeleteNull() {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);

		assertThatThrownBy(() -> service.delete(null))
				.isInstanceOf(ExceptionParametreNull.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>n'interagit ni avec le Gateway enfant
	 * ni avec le Gateway parent</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(blank) : ExceptionParametreBlank + message MESSAGE_PARAM_BLANK + aucune interaction gateway")
	public void testDeleteBlank() {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, ESPACES);

		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(parent blank) : violation de contrat structurel.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'interagit ni avec le Gateway enfant
	 * ni avec le Gateway parent</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(parent blank) : IllegalStateException + message MESSAGE_PAS_PARENT + aucune interaction gateway")
	public void testDeleteParentBlank() {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(ESPACES, OUTILLAGE);

		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verifyNoInteractions(gateway);
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(recherche parent technique KO avec message) :
	 * panne technique pendant la recherche du parent persistant.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + détail technique</li>
	 * <li>n'appelle jamais le Gateway enfant</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(recherche parent KO technique avec message) : propage l'exception + message KO_TECHNIQUE_RECHERCHE")
	public void testDeleteRechercheParentTechniqueKoAvecMessage()
			throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);
		final IllegalStateException panneTechnique =
				new IllegalStateException(MESSAGE_GATEWAY);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenThrow(panneTechnique);

		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(recherche parent technique KO sans message) :
	 * panne technique sans message pendant la recherche
	 * du parent persistant.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>n'appelle jamais le Gateway enfant</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(recherche parent KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testDeleteRechercheParentTechniqueKoSansMessage()
			throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(typeProduitGateway.findByLibelle(BAZAR)).thenThrow(panneTechnique);

		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(parent absent) : le parent requis
	 * n'existe pas en stockage.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'appelle jamais le Gateway enfant</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(parent absent) : IllegalStateException + message MESSAGE_PAS_PARENT")
	public void testDeleteParentAbsent() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(null);

		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(parent non persistant) : le parent retrouvé
	 * existe mais ne porte pas d'identifiant persistant.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'appelle jamais le Gateway enfant</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(parent non persistant) : IllegalStateException + message MESSAGE_PAS_PARENT")
	public void testDeleteParentNonPersistant() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentNonPersistant = new TypeProduit(BAZAR);
		parentNonPersistant.setIdTypeProduit(null);

		when(typeProduitGateway.findByLibelle(BAZAR))
				.thenReturn(parentNonPersistant);

		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(recherche enfants technique KO avec message) :
	 * panne technique pendant la recherche des enfants du parent.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + détail technique</li>
	 * <li>n'appelle jamais {@code gateway.delete(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(recherche enfants KO technique avec message) : propage l'exception + message KO_TECHNIQUE_RECHERCHE")
	public void testDeleteRechercheEnfantsTechniqueKoAvecMessage()
			throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final IllegalStateException panneTechnique =
				new IllegalStateException(MESSAGE_GATEWAY);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenThrow(panneTechnique);

		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, never()).delete(any(SousTypeProduit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(recherche enfants technique KO sans message) :
	 * panne technique sans message pendant la recherche
	 * des enfants du parent.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>n'appelle jamais {@code gateway.delete(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(recherche enfants KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testDeleteRechercheEnfantsTechniqueKoSansMessage()
			throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenThrow(panneTechnique);

		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, never()).delete(any(SousTypeProduit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(stockage null pendant ré-identification) :
	 * le Gateway retourne {@code null}
	 * pour les enfants du parent persistant.</p>
	 * <ul>
	 * <li>lève {@link ExceptionStockageVide}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_STOCKAGE_NULL}</li>
	 * <li>n'appelle jamais {@code gateway.delete(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(stockage null) : ExceptionStockageVide + message MESSAGE_STOCKAGE_NULL")
	public void testDeleteStockageNullPendantReidentification()
			throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class))).thenReturn(null);

		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionStockageVide.class);

		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, never()).delete(any(SousTypeProduit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(introuvable) : aucun objet persistant
	 * ne correspond au couple [parent, libellé].</p>
	 * <ul>
	 * <li>ne lève pas d'exception</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_INTROUVABLE}
	 * + libellé</li>
	 * <li>n'appelle jamais {@code gateway.delete(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(introuvable) : aucune exception + message MESSAGE_OBJ_INTROUVABLE + libellé")
	public void testDeleteIntrouvable() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit autre = new SousTypeProduit(VETEMENT, parentPersistant);
		autre.setIdSousTypeProduit(22L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(autre, null));

		service.delete(dto);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE
								+ OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, never()).delete(any(SousTypeProduit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(non persistant) : l'objet ré-identifié
	 * existe mais ne porte pas d'identifiant persistant.</p>
	 * <ul>
	 * <li>lève {@link ExceptionNonPersistant}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_OBJ_NON_PERSISTE}
	 * + libellé</li>
	 * <li>n'appelle jamais {@code gateway.delete(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(non persisté) : ExceptionNonPersistant + message MESSAGE_OBJ_NON_PERSISTE + libellé")
	public void testDeleteNonPersistant() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(null);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(existant));

		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionNonPersistant.class);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE
								+ OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, never()).delete(any(SousTypeProduit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(KO technique de suppression avec message) :
	 * le Gateway échoue pendant la destruction.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_DELETE_KO}
	 * + libellé + tiret + message technique</li>
	 * <li>délègue bien la destruction
	 * sur l'objet persistant retrouvé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(KO technique avec message) : exception relancée + message MESSAGE_DELETE_KO + détail technique")
	public void testDeleteTechniqueKoAvecMessage() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(20L);

		final Exception ex = new Exception(MESSAGE_GATEWAY);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(existant));
		doThrow(ex).when(gateway).delete(existant);

		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(ex);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_DELETE_KO
								+ OUTILLAGE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, times(1)).delete(existant);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(KO technique de suppression sans message) :
	 * le Gateway échoue sans message pendant la destruction.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>délègue bien la destruction
	 * sur l'objet persistant retrouvé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testDeleteTechniqueKoSansMessage() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit existant = new SousTypeProduit(OUTILLAGE, parentPersistant);
		existant.setIdSousTypeProduit(21L);

		final Exception ex = new Exception();

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(existant));
		doThrow(ex).when(gateway).delete(existant);

		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(ex);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_DELETE_KO
								+ OUTILLAGE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, times(1)).delete(existant);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(ok) : succès nominal complet.</p>
	 * <ul>
	 * <li>délègue la destruction
	 * sur l'objet persistant retrouvé</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_DELETE_OK}
	 * + libellé</li>
	 * <li>vérifie bien la ré-identification
	 * sur le couple [parent, libellé]</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(ok) : suppression déléguée + message MESSAGE_DELETE_OK + couple [parent, libellé]")
	public void testDeleteOk() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final TypeProduit parentPersistant = new TypeProduit(BAZAR);
		parentPersistant.setIdTypeProduit(10L);

		final SousTypeProduit autre = new SousTypeProduit(VETEMENT, parentPersistant);
		autre.setIdSousTypeProduit(30L);

		final SousTypeProduit cible = new SousTypeProduit(OUTILLAGE, parentPersistant);
		cible.setIdSousTypeProduit(31L);

		when(typeProduitGateway.findByLibelle(BAZAR)).thenReturn(parentPersistant);
		when(gateway.findAllByParent(any(TypeProduit.class)))
				.thenReturn(Arrays.asList(autre, null, cible));

		service.delete(dto);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.MESSAGE_DELETE_OK
								+ OUTILLAGE);

		verify(typeProduitGateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, times(1)).findAllByParent(any(TypeProduit.class));
		verify(gateway, times(1)).delete(cible);

	} // __________________________________________________________________	
	

	
	// ============================ TESTS count() ==========================

	
	
	/**
	 * <div>
	 * <p>count(KO technique avec message) :
	 * le Gateway échoue pendant le comptage
	 * avec un message exploitable.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + détail technique</li>
	 * <li>délègue une seule fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(KO technique avec message) : propage l'exception + message KO_TECHNIQUE_RECHERCHE + détail")
	public void testCountTechniqueKoAvecMessage() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final IllegalStateException panneTechnique =
				new IllegalStateException(MESSAGE_GATEWAY);

		when(gateway.count()).thenThrow(panneTechnique);

		assertThatThrownBy(() -> service.count())
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>count(KO technique sans message) :
	 * le Gateway échoue pendant le comptage
	 * sans message exploitable.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link SousTypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>délègue une seule fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testCountTechniqueKoSansMessage() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.count()).thenThrow(panneTechnique);

		assertThatThrownBy(() -> service.count())
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>count(retour négatif) :
	 * le Gateway retourne une valeur incohérente
	 * pour un comptage observable.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne un message technique explicite</li>
	 * <li>délègue une seule fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(retour négatif) : IllegalStateException + message technique explicite")
	public void testCountRetourNegatifIncoherent() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.count()).thenReturn(-1L);

		assertThatThrownBy(() -> service.count())
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(
						SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ SousTypeProduitICuService.TIRET_ESPACE
								+ "comptage négatif incohérent : -1");

		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>count(0) : aucun résultat en stockage.</p>
	 * <ul>
	 * <li>retourne {@code 0}</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>délègue une seule fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(0) : retourne 0 + message MESSAGE_RECHERCHE_VIDE")
	public void testCountZero() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.count()).thenReturn(0L);

		final long retour = service.count();

		assertThat(retour).isZero();
		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>count(positif) : succès nominal du comptage.</p>
	 * <ul>
	 * <li>retourne le comptage exact</li>
	 * <li>positionne exactement
	 * {@link SousTypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>délègue une seule fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(positif) : retourne le comptage exact + message MESSAGE_RECHERCHE_OK")
	public void testCountPositif() throws Exception {

		final SousTypeProduitGatewayIService gateway =
				mock(SousTypeProduitGatewayIService.class);
		final TypeProduitGatewayIService typeProduitGateway =
				mock(TypeProduitGatewayIService.class);
		final SousTypeProduitCuService service =
				new SousTypeProduitCuService(gateway, typeProduitGateway);

		when(gateway.count()).thenReturn(42L);

		final long retour = service.count();

		assertThat(retour).isEqualTo(42L);
		assertThat(service.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(gateway, times(1)).count();
		verifyNoInteractions(typeProduitGateway);

	} // __________________________________________________________________
	
	

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
		
	} // __________________________________________________________________
	
	

}
