package levy.daniel.application.model.services.produittype.cu.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import levy.daniel.application.model.dto.produittype.ProduitDTO;
import levy.daniel.application.model.dto.produittype.ProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.ProduitDTO.OutputDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.metier.produittype.Produit;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.cu.ProduitICuService;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionDoublon;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionNonPersistant;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreBlank;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreNull;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionStockageVide;
import levy.daniel.application.model.services.produittype.gateway.ProduitGatewayIService;
import levy.daniel.application.model.services.produittype.gateway.SousTypeProduitGatewayIService;
import levy.daniel.application.model.services.produittype.pagination.RequetePage;
import levy.daniel.application.model.services.produittype.pagination.ResultatPage;

/**
 * <div>
 * <p style="font-weight:bold;">CLASSE ProduitCuServiceMockTest.java :</p>
 * <p>Tests JUnit Mockito ciblés sur creer(...) pour
 * {@link ProduitCuService}.</p>
 * <p>Vérifie l'implémentation du contrat du PORT
 * {@link ProduitICuService} et la délégation vers
 * {@link ProduitGatewayIService}.</p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 30 mars 2026
 */
@ExtendWith(MockitoExtension.class)
public class ProduitCuServiceMockTest {

	/** Tag JUnit : tests Mockito de la couche CU. */
	public static final String TAG = "cu-mock";

	/** TypeProduit parent : "bazar". */
	public static final String BAZAR = "bazar";

	/** SousTypeProduit parent : "outillage". */
	public static final String OUTILLAGE = "outillage";

	/** Produit : "marteau". */
	public static final String MARTEAU = "marteau";
	
	/**
	 * "quincaillerie"
	 */
	public static final String QUINCAILLERIE = "quincaillerie";
	
	/**
	 * "atelier"
	 */
	public static final String ATELIER = "atelier";
	
	/**
	 * "scie"
	 */
	public static final String SCIE = "scie";

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
	public ProduitCuServiceMockTest() {
		super();
	}

	
	
	// ============================ TESTS creer(...) =======================

	
	
	/**
	 * <div>
	 * <p>creer(null) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_CREER_NULL}</li>
	 * <li>n'interagit ni avec le gateway Produit
	 * ni avec le gateway SousTypeProduit</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(null) : retourne null, message utilisateur, aucune interaction gateway")
	public void testCreerNull() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final OutputDTO retour = service.creer(null);

		assertThat(retour).isNull();
		assertThat(service.getMessage()).isEqualTo(ProduitICuService.MESSAGE_CREER_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeGateway);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div><p>creer(blank) : violation de contrat applicatif.</p></div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(blank) : ExceptionParametreBlank + message exact + aucune interaction gateway")
	public void testCreerBlank() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);
		final InputDTO dto = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, ESPACES);

		assertThatThrownBy(() -> service.creer(dto))
			.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(service.getMessage()).isEqualTo(ProduitICuService.MESSAGE_CREER_NOM_BLANK);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeGateway);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div><p>creer(parent blank) : IllegalStateException + message exact.</p></div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(parent blank) : IllegalStateException + message exact MESSAGE_PAS_PARENT + aucune interaction gateway")
	public void testCreerParentBlank() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);
		final InputDTO dto = new ProduitDTO.InputDTO(BAZAR, ESPACES, MARTEAU);

		assertThatThrownBy(() -> service.creer(dto))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(service.getMessage()).isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeGateway);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div><p>creer(controle technique KO avec message) : propage l'exception et rationalise le message utilisateur.</p></div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(controle technique KO avec message) : propage l'exception et rationalise le message")
	public void testCreerControleTechniqueKoAvecMessage() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);
		final InputDTO dto = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);

		when(gateway.findByLibelle(MARTEAU)).thenThrow(panneTechnique);

		assertThatThrownBy(() -> service.creer(dto))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage()).isEqualTo(
				ProduitICuService.PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER + MESSAGE_GATEWAY);

		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(gateway, never()).creer(any(Produit.class));
		verifyNoInteractions(sousTypeGateway);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div><p>creer(doublon) : refus métier d'unicité.</p></div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(doublon) : ExceptionDoublon + message exact + aucune création gateway")
	public void testCreerDoublon() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);
		final InputDTO dto = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);
		final SousTypeProduit sousTypeProduit = new SousTypeProduit(OUTILLAGE, typeProduit);
		sousTypeProduit.setIdSousTypeProduit(10L);
		final Produit existant = new Produit(MARTEAU, sousTypeProduit);
		existant.setIdProduit(100L);

		when(gateway.findByLibelle(MARTEAU)).thenReturn(Arrays.asList(existant));

		assertThatThrownBy(() -> service.creer(dto))
			.isInstanceOf(ExceptionDoublon.class);

		assertThat(service.getMessage()).isEqualTo(ProduitICuService.MESSAGE_DOUBLON + MARTEAU);

		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(gateway, never()).creer(any(Produit.class));
		verifyNoInteractions(sousTypeGateway);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div><p>creer(parent technique KO avec message) : propage l'exception et rationalise le message utilisateur.</p></div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(parent technique KO avec message) : propage l'exception et rationalise le message")
	public void testCreerParentTechniqueKoAvecMessage() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);
		final InputDTO dto = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY_BIS);

		when(gateway.findByLibelle(MARTEAU)).thenReturn(Collections.emptyList());
		when(sousTypeGateway.findByLibelle(OUTILLAGE)).thenThrow(panneTechnique);

		assertThatThrownBy(() -> service.creer(dto))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage()).isEqualTo(
				ProduitICuService.PREFIX_MESSAGE_PARENT_TECHNIQUE_CREER + MESSAGE_GATEWAY_BIS);

		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).creer(any(Produit.class));
		
	} // __________________________________________________________________
	
	

	/**
	 * <div><p>creer(parent absent) : IllegalStateException + message exact MESSAGE_PAS_PARENT.</p></div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(parent absent) : IllegalStateException + message exact MESSAGE_PAS_PARENT")
	public void testCreerParentAbsent() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);
		final InputDTO dto = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);

		when(gateway.findByLibelle(MARTEAU)).thenReturn(Collections.emptyList());
		when(sousTypeGateway.findByLibelle(OUTILLAGE)).thenReturn(Collections.emptyList());

		assertThatThrownBy(() -> service.creer(dto))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(service.getMessage()).isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verify(sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).creer(any(Produit.class));
		
	} // __________________________________________________________________
	
	

	/**
	 * <div><p>creer(creation technique KO avec message) : propage l'exception du gateway et rationalise le message.</p></div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(creation technique KO avec message) : propage l'exception du gateway et rationalise le message")
	public void testCreerCreationTechniqueKoAvecMessage() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);
		final InputDTO dto = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException panneTechnique = new IllegalStateException(MESSAGE_GATEWAY);

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);
		final SousTypeProduit parentPersistant = new SousTypeProduit(OUTILLAGE, typeProduit);
		parentPersistant.setIdSousTypeProduit(10L);

		when(gateway.findByLibelle(MARTEAU)).thenReturn(Collections.emptyList());
		when(sousTypeGateway.findByLibelle(OUTILLAGE)).thenReturn(Arrays.asList(parentPersistant));
		when(gateway.creer(any(Produit.class))).thenThrow(panneTechnique);

		assertThatThrownBy(() -> service.creer(dto))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage()).isEqualTo(
				ProduitICuService.PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER + MESSAGE_GATEWAY);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div><p>creer(gateway retourne null) : IllegalStateException + message exact MESSAGE_CREATION_TECHNIQUE_KO_CREER.</p></div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(gateway retourne null) : IllegalStateException + message exact MESSAGE_CREATION_TECHNIQUE_KO_CREER")
	public void testCreerRetourGatewayNull() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);
		final InputDTO dto = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);
		final SousTypeProduit parentPersistant = new SousTypeProduit(OUTILLAGE, typeProduit);
		parentPersistant.setIdSousTypeProduit(10L);

		when(gateway.findByLibelle(MARTEAU)).thenReturn(Collections.emptyList());
		when(sousTypeGateway.findByLibelle(OUTILLAGE)).thenReturn(Arrays.asList(parentPersistant));
		when(gateway.creer(any(Produit.class))).thenReturn(null);

		assertThatThrownBy(() -> service.creer(dto))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage(ProduitICuService.MESSAGE_CREATION_TECHNIQUE_KO_CREER);

		assertThat(service.getMessage()).isEqualTo(ProduitICuService.MESSAGE_CREATION_TECHNIQUE_KO_CREER);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div><p>creer(ok) : retourne l'OutputDTO créé et positionne MESSAGE_CREER_OK.</p></div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(ok) : retourne OutputDTO + message MESSAGE_CREER_OK + délégation gateway")
	public void testCreerOk() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);
		final InputDTO dto = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);
		final SousTypeProduit parentPersistant = new SousTypeProduit(OUTILLAGE, typeProduit);
		parentPersistant.setIdSousTypeProduit(10L);
		final Produit cree = new Produit(MARTEAU, parentPersistant);
		cree.setIdProduit(100L);

		when(gateway.findByLibelle(MARTEAU)).thenReturn(Collections.emptyList());
		when(sousTypeGateway.findByLibelle(OUTILLAGE)).thenReturn(Arrays.asList(parentPersistant));
		when(gateway.creer(any(Produit.class))).thenReturn(cree);

		final OutputDTO retour = service.creer(dto);

		assertThat(retour).isNotNull();
		assertThat(retour.getIdProduit()).isEqualTo(100L);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(retour.getProduit()).isEqualTo(MARTEAU);
		assertThat(service.getMessage()).isEqualTo(ProduitICuService.MESSAGE_CREER_OK);

		final ArgumentCaptor<Produit> captor = ArgumentCaptor.forClass(Produit.class);
		verify(gateway, times(1)).creer(captor.capture());
		final Produit produitPasseAuGateway = captor.getValue();
		assertThat(produitPasseAuGateway).isNotNull();
		assertThat(produitPasseAuGateway.getProduit()).isEqualTo(MARTEAU);
		assertThat(produitPasseAuGateway.getSousTypeProduit()).isNotNull();
		assertThat(produitPasseAuGateway.getSousTypeProduit().getIdSousTypeProduit()).isEqualTo(10L);
		
	} // __________________________________________________________________
	
	
	
	// ========================= TESTS rechercherTous() ====================
	
	
	
	/**
	 * <div>
	 * <p>rechercherTous() : stockage null.</p>
	 * <ul>
	 * <li>lève {@link ExceptionStockageVide}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_STOCKAGE_NULL}</li>
	 * <li>délègue une seule fois à {@code gateway.rechercherTous()}</li>
	 * <li>n'interagit jamais avec le gateway SousTypeProduit</li>
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
		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		when(gateway.rechercherTous()).thenReturn(null);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTous())
			.isInstanceOf(ExceptionStockageVide.class)
			.hasMessage(ProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_STOCKAGE_NULL);

		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTous() : recherche technique KO avec message.</p>
	 * <ul>
	 * <li>propage l'exception du gateway</li>
	 * <li>rationalise le message utilisateur avec
	 * {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}</li>
	 * <li>n'interagit jamais avec le gateway SousTypeProduit</li>
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
		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final IllegalStateException panneTechnique
			= new IllegalStateException(MESSAGE_GATEWAY);

		when(gateway.rechercherTous()).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTous())
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				ProduitICuService.KO_TECHNIQUE_RECHERCHE
					+ ProduitICuService.TIRET_ESPACE
					+ MESSAGE_GATEWAY);

		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTous() : recherche technique KO sans message.</p>
	 * <ul>
	 * <li>propage exactement l'exception du gateway</li>
	 * <li>retombe sur
	 * {@link ProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>n'interagit jamais avec le gateway SousTypeProduit</li>
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
		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.rechercherTous()).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTous())
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				ProduitICuService.KO_TECHNIQUE_RECHERCHE
					+ ProduitICuService.TIRET_ESPACE
					+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTous() : résultats vides après filtrage.</p>
	 * <ul>
	 * <li>le gateway retourne uniquement des éléments {@code null}</li>
	 * <li>retourne une liste vide mais non {@code null}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>n'interagit jamais avec le gateway SousTypeProduit</li>
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
		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		when(gateway.rechercherTous()).thenReturn(Arrays.asList(null, null));

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = service.rechercherTous();
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull().isEmpty();
		assertThat(message).isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeGateway);

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
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>n'interagit jamais avec le gateway SousTypeProduit</li>
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
		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);

		final SousTypeProduit parent = new SousTypeProduit(OUTILLAGE, typeProduit);
		parent.setIdSousTypeProduit(10L);

		final Produit produitScie = new Produit(SCIE, parent);
		produitScie.setIdProduit(2L);

		final Produit produitMarteau = new Produit(MARTEAU, parent);
		produitMarteau.setIdProduit(1L);

		when(gateway.rechercherTous())
			.thenReturn(Arrays.asList(
				produitScie,
				null,
				produitMarteau,
				produitScie));

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = service.rechercherTous();
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);

		assertThat(retour)
			.extracting(ProduitDTO.OutputDTO::getProduit)
			.containsExactly(MARTEAU, SCIE);

		assertThat(retour)
			.extracting(ProduitDTO.OutputDTO::getIdProduit)
			.containsExactly(1L, 2L);

		assertThat(retour)
			.extracting(ProduitDTO.OutputDTO::getSousTypeProduit)
			.containsExactly(OUTILLAGE, OUTILLAGE);

		assertThat(retour)
			.extracting(ProduitDTO.OutputDTO::getTypeProduit)
			.containsExactly(BAZAR, BAZAR);

		assertThat(message).isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(gateway, times(1)).rechercherTous();
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________

	
	
	// ===================== TESTS rechercherTousString() ==================
	/**
	 * <div>
	 * <p>rechercherTousString() : résultats vides.</p>
	 * <ul>
	 * <li>délègue à rechercherTous() ;</li>
	 * <li>retourne une liste vide mais non {@code null} ;</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousString() : liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testRechercherTousStringVide() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = spy(new ProduitCuService(gateway, sousTypeGateway));

		doReturn(Collections.emptyList()).when(service).rechercherTous();

		/* ======================= ACT ======================= */
		final List<String> retour = service.rechercherTousString();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull().isEmpty();
		assertThat(service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(service, times(1)).rechercherTous();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousString() : scénario nominal.</p>
	 * <ul>
	 * <li>délègue à rechercherTous() ;</li>
	 * <li>extrait uniquement les libellés Produit exploitables ;</li>
	 * <li>retire les éventuels {@code null} et blank ;</li>
	 * <li>retourne une liste cohérente ;</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_OK}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousString() : extraction des libellés + filtre null/blank + message MESSAGE_RECHERCHE_OK")
	public void testRechercherTousStringOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = spy(new ProduitCuService(gateway, sousTypeGateway));

		final OutputDTO dto1 = new ProduitDTO.OutputDTO();
		dto1.setIdProduit(1L);
		dto1.setTypeProduit("MATERIEL");
		dto1.setSousTypeProduit("OUTILLAGE");
		dto1.setProduit("MARTEAU");

		final OutputDTO dto2 = new ProduitDTO.OutputDTO();
		dto2.setIdProduit(2L);
		dto2.setTypeProduit("MATERIEL");
		dto2.setSousTypeProduit("OUTILLAGE");
		dto2.setProduit("SCIE");

		final OutputDTO dto3 = new ProduitDTO.OutputDTO();
		dto3.setIdProduit(3L);
		dto3.setTypeProduit("MATERIEL");
		dto3.setSousTypeProduit("OUTILLAGE");
		dto3.setProduit("   ");

		doReturn(Arrays.asList(dto1, null, dto2, dto3)).when(service).rechercherTous();

		/* ======================= ACT ======================= */
		final List<String> retour = service.rechercherTousString();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).containsExactly("MARTEAU", "SCIE");
		assertThat(service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(service, times(1)).rechercherTous();

	} // __________________________________________________________________
	
	

	// ================== TESTS rechercherTousParPage(...) =================
	
	
	
	/**
	 * <div>
	 * <p>rechercherTousParPage() : requête null.</p>
	 * <ul>
	 * <li>refuse immédiatement le traitement ;</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PAGEABLE_NULL} ;</li>
	 * <li>n'appelle aucun GATEWAY.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousParPage(null) : IllegalStateException + MESSAGE_PAGEABLE_NULL")
	public void testRechercherTousParPageNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTousParPage(null))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage(ProduitICuService.MESSAGE_PAGEABLE_NULL);

		assertThat(service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_PAGEABLE_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousParPage() : KO technique avec message.</p>
	 * <ul>
	 * <li>propage l'exception technique ;</li>
	 * <li>rationalise le message utilisateur avec
	 * {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE} ;</li>
	 * <li>n'interagit jamais avec le gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousParPage() : gateway KO avec message")
	public void testRechercherTousParPageKoTechniqueAvecMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);
		final RequetePage requete = new RequetePage(0, 4);

		final IllegalStateException panneTechnique
			= new IllegalStateException(MESSAGE_GATEWAY);

		when(gateway.rechercherTousParPage(requete)).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTousParPage(requete))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				ProduitICuService.KO_TECHNIQUE_RECHERCHE
					+ ProduitICuService.TIRET_ESPACE
					+ MESSAGE_GATEWAY);

		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________
	


	/**
	 * <div>
	 * <p>rechercherTousParPage() : KO technique sans message.</p>
	 * <ul>
	 * <li>propage l'exception technique ;</li>
	 * <li>retombe sur
	 * {@link ProduitICuService#MSG_ERREUR_NON_SPECIFIEE} ;</li>
	 * <li>rationalise le message utilisateur avec
	 * {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE} ;</li>
	 * <li>n'interagit jamais avec le gateway SousTypeProduit.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousParPage() : gateway KO sans message")
	public void testRechercherTousParPageKoTechniqueSansMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);
		final RequetePage requete = new RequetePage(0, 4);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.rechercherTousParPage(requete)).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTousParPage(requete))
			.isSameAs(panneTechnique);

		assertThat(service.getMessage())
			.isEqualTo(
				ProduitICuService.KO_TECHNIQUE_RECHERCHE
					+ ProduitICuService.TIRET_ESPACE
					+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________
	


	/**
	 * <div>
	 * <p>rechercherTousParPage() : résultat technique null.</p>
	 * <ul>
	 * <li>refuse une page technique null ;</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_PAGINEE_KO}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousParPage() : gateway retourne null")
	public void testRechercherTousParPageRetourNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);
		final RequetePage requete = mock(RequetePage.class);

		when(gateway.rechercherTousParPage(requete)).thenReturn(null);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTousParPage(requete))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_KO);

		assertThat(service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_KO);

		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(sousTypeGateway);

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
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_PAGINEE_OK}</li>
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
		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final RequetePage requete = new RequetePage(0, 4);

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);

		final SousTypeProduit parent = new SousTypeProduit(OUTILLAGE, typeProduit);
		parent.setIdSousTypeProduit(10L);

		final Produit produitScie = new Produit(SCIE, parent);
		produitScie.setIdProduit(2L);

		final Produit produitMarteau = new Produit(MARTEAU, parent);
		produitMarteau.setIdProduit(1L);

		final ResultatPage<Produit> resultatGateway
				= new ResultatPage<Produit>(
						Arrays.asList(produitScie, null, produitMarteau, produitScie),
						0,
						4,
						10L);

		when(gateway.rechercherTousParPage(requete)).thenReturn(resultatGateway);

		/* ======================= ACT ======================= */
		final ResultatPage<OutputDTO> retour
				= service.rechercherTousParPage(requete);

		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour.getPageNumber()).isEqualTo(0);
		assertThat(retour.getPageSize()).isEqualTo(4);
		assertThat(retour.getTotalElements()).isEqualTo(10L);

		assertThat(retour.getContent()).isNotNull();
		assertThat(retour.getContent()).hasSize(2);

		assertThat(retour.getContent())
				.extracting(OutputDTO::getProduit)
				.containsExactly(MARTEAU, SCIE);

		assertThat(retour.getContent())
				.extracting(OutputDTO::getIdProduit)
				.containsExactly(1L, 2L);

		assertThat(retour.getContent())
				.extracting(OutputDTO::getSousTypeProduit)
				.containsExactly(OUTILLAGE, OUTILLAGE);

		assertThat(retour.getContent())
				.extracting(OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, BAZAR);

		assertThat(message)
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		verify(gateway, times(1)).rechercherTousParPage(requete);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________	
	

	
	// ======================= TESTS findByLibelle(...) ====================
	
	
	
	/**
	 * <div>
	 * <p>findByLibelle(blank) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>n'interagit avec aucun gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(blank) : retourne null + message MESSAGE_PARAM_BLANK + aucune interaction gateway")
	public void testFindByLibelleBlank() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final List<OutputDTO> dtos = service.findByLibelle(ESPACES);

		assertThat(dtos).isNull();
		assertThat(service.getMessage()).isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(gateway retourne null) : anomalie technique.</p>
	 * <ul>
	 * <li>lève une exception technique</li>
	 * <li>positionne {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(gateway retourne null) : RuntimeException + message KO_TECHNIQUE_RECHERCHE")
	public void testFindByLibelleGatewayRetourNull() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		when(gateway.findByLibelle(MARTEAU)).thenReturn(null);

		assertThatThrownBy(() -> service.findByLibelle(MARTEAU))
			.isInstanceOf(RuntimeException.class);

		assertThat(service.getMessage()).isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE);

		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(introuvable) : recherche vide.</p>
	 * <ul>
	 * <li>retourne une liste vide mais non {@code null}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(introuvable) : liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testFindByLibelleIntrouvable() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		when(gateway.findByLibelle(MARTEAU)).thenReturn(Collections.emptyList());

		final List<OutputDTO> dtos = service.findByLibelle(MARTEAU);

		assertThat(dtos).isNotNull().isEmpty();
		assertThat(service.getMessage()).isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(ok) : recherche exacte non unique.</p>
	 * <ul>
	 * <li>retourne une liste non nulle</li>
	 * <li>retire les {@code null}</li>
	 * <li>conserve les deux parents distincts</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(ok) : liste non nulle + filtre nulls + message MESSAGE_RECHERCHE_OK")
	public void testFindByLibelleOk() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final TypeProduit typeProduitA = new TypeProduit(BAZAR);
		typeProduitA.setIdTypeProduit(1L);
		final SousTypeProduit parentA = new SousTypeProduit(OUTILLAGE, typeProduitA);
		parentA.setIdSousTypeProduit(10L);

		final TypeProduit typeProduitB = new TypeProduit(QUINCAILLERIE);
		typeProduitB.setIdTypeProduit(2L);
		final SousTypeProduit parentB = new SousTypeProduit(ATELIER, typeProduitB);
		parentB.setIdSousTypeProduit(20L);

		final Produit produitA = new Produit(MARTEAU, parentA);
		produitA.setIdProduit(100L);

		final Produit produitB = new Produit(MARTEAU, parentB);
		produitB.setIdProduit(200L);

		when(gateway.findByLibelle(MARTEAU))
			.thenReturn(Arrays.asList(produitA, null, produitB));

		final List<OutputDTO> dtos = service.findByLibelle(MARTEAU);

		assertThat(dtos).isNotNull();
		assertThat(dtos).hasSize(2);
		assertThat(service.getMessage()).isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

		assertThat(dtos)
			.extracting(OutputDTO::getProduit)
			.containsExactlyInAnyOrder(MARTEAU, MARTEAU);

		assertThat(dtos)
			.extracting(OutputDTO::getSousTypeProduit)
			.containsExactlyInAnyOrder(OUTILLAGE, ATELIER);

		verify(gateway, times(1)).findByLibelle(MARTEAU);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________

	
	
	// ===================== TESTS findByLibelleRapide(...) =================
	
	
	
	/**
	 * <div>
	 * <p>findByLibelleRapide(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'interagit avec aucun gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(null) : IllegalStateException + message MESSAGE_PARAM_NULL + aucune interaction gateway")
	public void testFindByLibelleRapideNull() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		assertThatThrownBy(() -> service.findByLibelleRapide(null))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage(ProduitICuService.MESSAGE_PARAM_NULL);

		assertThat(service.getMessage()).isEqualTo(ProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelleRapide(blank) : délègue à rechercherTous().</p>
	 * <ul>
	 * <li>retourne la liste préparée par rechercherTous()</li>
	 * <li>conserve le message positionné par rechercherTous()</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(blank) : délègue à rechercherTous()")
	public void testFindByLibelleRapideBlank() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = spy(new ProduitCuService(gateway, sousTypeGateway));

		final OutputDTO dto = new ProduitDTO.OutputDTO();
		dto.setIdProduit(1L);
		dto.setTypeProduit(BAZAR);
		dto.setSousTypeProduit(OUTILLAGE);
		dto.setProduit(MARTEAU);

		doReturn(Arrays.asList(dto)).when(service).rechercherTous();

		final List<OutputDTO> retour = service.findByLibelleRapide(ESPACES);

		assertThat(retour).isNotNull().hasSize(1);
		assertThat(retour.get(0).getProduit()).isEqualTo(MARTEAU);

		verify(service, times(1)).rechercherTous();
		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelleRapide(gateway retourne null) : anomalie technique.</p>
	 * <ul>
	 * <li>lève une exception technique</li>
	 * <li>positionne {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(gateway retourne null) : RuntimeException + message KO_TECHNIQUE_RECHERCHE")
	public void testFindByLibelleRapideGatewayRetourNull() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		when(gateway.findByLibelleRapide("mar")).thenReturn(null);

		assertThatThrownBy(() -> service.findByLibelleRapide("mar"))
			.isInstanceOf(RuntimeException.class);

		assertThat(service.getMessage()).isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE);

		verify(gateway, times(1)).findByLibelleRapide("mar");
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelleRapide(introuvable) : recherche vide.</p>
	 * <ul>
	 * <li>retourne une liste vide mais non {@code null}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(introuvable) : liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testFindByLibelleRapideIntrouvable() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		when(gateway.findByLibelleRapide("zzz")).thenReturn(Collections.emptyList());

		final List<OutputDTO> dtos = service.findByLibelleRapide("zzz");

		assertThat(dtos).isNotNull().isEmpty();
		assertThat(service.getMessage()).isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(gateway, times(1)).findByLibelleRapide("zzz");
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelleRapide(ok) : recherche rapide non vide.</p>
	 * <ul>
	 * <li>retire les {@code null}</li>
	 * <li>trie les objets métier</li>
	 * <li>retourne une liste non nulle</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(ok) : liste non nulle + filtre nulls + message MESSAGE_RECHERCHE_OK")
	public void testFindByLibelleRapideOk() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);

		final SousTypeProduit parent = new SousTypeProduit(OUTILLAGE, typeProduit);
		parent.setIdSousTypeProduit(10L);

		final Produit produitScie = new Produit(SCIE, parent);
		produitScie.setIdProduit(2L);

		final Produit produitMarteau = new Produit(MARTEAU, parent);
		produitMarteau.setIdProduit(1L);

		when(gateway.findByLibelleRapide("a"))
			.thenReturn(Arrays.asList(produitScie, null, produitMarteau));

		final List<OutputDTO> dtos = service.findByLibelleRapide("a");

		assertThat(dtos).isNotNull();
		assertThat(dtos).hasSize(2);
		assertThat(service.getMessage()).isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

		assertThat(dtos)
			.extracting(OutputDTO::getProduit)
			.containsExactly(MARTEAU, SCIE);

		verify(gateway, times(1)).findByLibelleRapide("a");
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________
	
	
	
	// ===================== TESTS findAllByParent(...) ====================
	
	
	
	/**
	 * <div>
	 * <p>findAllByParent(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève une {@link RuntimeException}</li>
	 * <li>positionne {@link ProduitICuService#RECHERCHE_SOUSTYPEPRODUIT_NULL}</li>
	 * <li>n'interagit avec aucun gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(null) : RuntimeException + message RECHERCHE_SOUSTYPEPRODUIT_NULL + aucune interaction gateway")
	public void testFindAllByParentNull() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		assertThatThrownBy(() -> service.findAllByParent(null))
			.isInstanceOf(RuntimeException.class)
			.hasMessage(ProduitICuService.RECHERCHE_SOUSTYPEPRODUIT_NULL);

		assertThat(service.getMessage())
			.isEqualTo(ProduitICuService.RECHERCHE_SOUSTYPEPRODUIT_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(parent blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'interagit avec aucun gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(parent blank) : IllegalStateException + message MESSAGE_PAS_PARENT + aucune interaction gateway")
	public void testFindAllByParentParentBlank() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final SousTypeProduitDTO.InputDTO parentDto
			= new SousTypeProduitDTO.InputDTO(BAZAR, ESPACES);

		assertThatThrownBy(() -> service.findAllByParent(parentDto))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(parent absent/non persistant) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(parent absent) : IllegalStateException + message MESSAGE_PAS_PARENT")
	public void testFindAllByParentPasParent() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final SousTypeProduitDTO.InputDTO parentDto
			= new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(sousTypeGateway.findByLibelle(OUTILLAGE))
			.thenReturn(Arrays.asList((SousTypeProduit) null));

		assertThatThrownBy(() -> service.findAllByParent(parentDto))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		verify(sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(gateway retourne null) : anomalie technique.</p>
	 * <ul>
	 * <li>lève une {@link RuntimeException}</li>
	 * <li>positionne {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(gateway retourne null) : RuntimeException + message KO_TECHNIQUE_RECHERCHE")
	public void testFindAllByParentGatewayRetourNull() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);
		final SousTypeProduit parentPersistant = new SousTypeProduit(OUTILLAGE, typeProduit);
		parentPersistant.setIdSousTypeProduit(10L);

		final SousTypeProduitDTO.InputDTO parentDto
			= new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(sousTypeGateway.findByLibelle(OUTILLAGE))
			.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findAllByParent(parentPersistant)).thenReturn(null);

		assertThatThrownBy(() -> service.findAllByParent(parentDto))
			.isInstanceOf(RuntimeException.class);

		assertThat(service.getMessage())
			.isEqualTo(ProduitICuService.KO_TECHNIQUE_RECHERCHE);

		verify(sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findAllByParent(parentPersistant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(introuvable) : recherche vide.</p>
	 * <ul>
	 * <li>retourne une liste vide mais non {@code null}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(introuvable) : liste vide + message MESSAGE_RECHERCHE_VIDE")
	public void testFindAllByParentIntrouvable() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);
		final SousTypeProduit parentPersistant = new SousTypeProduit(OUTILLAGE, typeProduit);
		parentPersistant.setIdSousTypeProduit(10L);

		final SousTypeProduitDTO.InputDTO parentDto
			= new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(sousTypeGateway.findByLibelle(OUTILLAGE))
			.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findAllByParent(parentPersistant))
			.thenReturn(Collections.emptyList());

		final List<OutputDTO> dtos = service.findAllByParent(parentDto);

		assertThat(dtos).isNotNull().isEmpty();
		assertThat(service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findAllByParent(parentPersistant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(ok) : recherche nominale par parent.</p>
	 * <ul>
	 * <li>retourne une liste non nulle ;</li>
	 * <li>retire les {@code null} ;</li>
	 * <li>trie les objets métier ;</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_RECHERCHE_OK}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(ok) : liste non nulle + filtre nulls + message MESSAGE_RECHERCHE_OK")
	public void testFindAllByParentOk() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);
		final SousTypeProduit parentPersistant = new SousTypeProduit(OUTILLAGE, typeProduit);
		parentPersistant.setIdSousTypeProduit(10L);

		final Produit produitScie = new Produit(SCIE, parentPersistant);
		produitScie.setIdProduit(2L);

		final Produit produitMarteau = new Produit(MARTEAU, parentPersistant);
		produitMarteau.setIdProduit(1L);

		final SousTypeProduitDTO.InputDTO parentDto
			= new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(sousTypeGateway.findByLibelle(OUTILLAGE))
			.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findAllByParent(parentPersistant))
			.thenReturn(Arrays.asList(produitScie, null, produitMarteau));

		final List<OutputDTO> dtos = service.findAllByParent(parentDto);

		assertThat(dtos).isNotNull();
		assertThat(dtos).hasSize(2);
		assertThat(service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

		assertThat(dtos)
			.extracting(OutputDTO::getProduit)
			.containsExactly(MARTEAU, SCIE);

		assertThat(dtos)
			.extracting(OutputDTO::getSousTypeProduit)
			.containsExactly(OUTILLAGE, OUTILLAGE);

		verify(sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findAllByParent(parentPersistant);

	} // __________________________________________________________________	

	
	
	// ========================== TESTS findByDTO(...) =====================
	
	
	
	/**
	 * <div>
	 * <p>findByDTO(null) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_RECHERCHE_OBJ_NULL}</li>
	 * <li>n'interagit avec aucun gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(null) : retourne null + message MESSAGE_RECHERCHE_OBJ_NULL + aucune interaction gateway")
	public void testFindByDTONull() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final OutputDTO dto = service.findByDTO(null);

		assertThat(dto).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OBJ_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(parent blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'interagit avec aucun gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(parent blank) : IllegalStateException + message MESSAGE_PAS_PARENT + aucune interaction gateway")
	public void testFindByDTOParentBlank() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final InputDTO dto = new ProduitDTO.InputDTO(BAZAR, ESPACES, MARTEAU);

		assertThatThrownBy(() -> service.findByDTO(dto))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage(ProduitICuService.MESSAGE_PAS_PARENT);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(parent absent) : aucun résultat possible.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>n'appelle pas la recherche Produit par parent</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(parent absent) : retourne null + message MESSAGE_RECHERCHE_VIDE")
	public void testFindByDTOParentAbsent() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final InputDTO dto = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);

		when(sousTypeGateway.findByLibelle(OUTILLAGE))
			.thenReturn(Collections.emptyList());

		final OutputDTO retour = service.findByDTO(dto);

		assertThat(retour).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).findAllByParent(any(SousTypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(introuvable) : aucun Produit exact trouvé.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(introuvable) : retourne null + message MESSAGE_RECHERCHE_VIDE")
	public void testFindByDTOIntrouvable() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);
		final SousTypeProduit parentPersistant = new SousTypeProduit(OUTILLAGE, typeProduit);
		parentPersistant.setIdSousTypeProduit(10L);

		final Produit produitScie = new Produit(SCIE, parentPersistant);
		produitScie.setIdProduit(2L);

		final InputDTO dto = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);

		when(sousTypeGateway.findByLibelle(OUTILLAGE))
			.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findAllByParent(parentPersistant))
			.thenReturn(Arrays.asList(produitScie));

		final OutputDTO retour = service.findByDTO(dto);

		assertThat(retour).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findAllByParent(parentPersistant);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(ok) : retourne l'OutputDTO exact correspondant au DTO de recherche.</p>
	 * <ul>
	 * <li>retire les {@code null}</li>
	 * <li>cherche le match exact sur le libellé Produit</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_SUCCES_RECHERCHE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(ok) : retourne OutputDTO exact + message MESSAGE_SUCCES_RECHERCHE")
	public void testFindByDTOOk() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);
		final SousTypeProduit parentPersistant = new SousTypeProduit(OUTILLAGE, typeProduit);
		parentPersistant.setIdSousTypeProduit(10L);

		final Produit produitScie = new Produit(SCIE, parentPersistant);
		produitScie.setIdProduit(2L);

		final Produit produitMarteau = new Produit(MARTEAU, parentPersistant);
		produitMarteau.setIdProduit(1L);

		final InputDTO dto = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);

		when(sousTypeGateway.findByLibelle(OUTILLAGE))
			.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findAllByParent(parentPersistant))
			.thenReturn(Arrays.asList(produitScie, null, produitMarteau));

		final OutputDTO retour = service.findByDTO(dto);

		assertThat(retour).isNotNull();
		assertThat(retour.getIdProduit()).isEqualTo(1L);
		assertThat(retour.getProduit()).isEqualTo(MARTEAU);
		assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		verify(sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findAllByParent(parentPersistant);

	} // __________________________________________________________________
		

	
	// ======================== TESTS findById(...) ========================
	
	
	
	/**
	 * <div>
	 * <p>findById(null) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'interagit avec aucun gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(null) : retourne null + message MESSAGE_PARAM_NULL + aucune interaction gateway")
	public void testFindByIdNull() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final OutputDTO dto = service.findById(null);

		assertThat(dto).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findById(introuvable) : cas nominal de non-trouvabilité.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_OBJ_INTROUVABLE} + id</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(introuvable) : retourne null + message exact MESSAGE_OBJ_INTROUVABLE + id")
	public void testFindByIdIntrouvable() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final Long id = 999L;

		when(gateway.findById(id)).thenReturn(null);

		final OutputDTO dto = service.findById(id);

		assertThat(dto).isNull();
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_OBJ_INTROUVABLE + id);

		verify(gateway, times(1)).findById(id);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findById(ok) : retourne l'OutputDTO exact correspondant à l'identifiant demandé.</p>
	 * <ul>
	 * <li>délègue au gateway Produit ;</li>
	 * <li>convertit l'objet métier en OutputDTO ;</li>
	 * <li>positionne {@link ProduitICuService#MESSAGE_SUCCES_RECHERCHE}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(ok) : retourne OutputDTO exact + message MESSAGE_SUCCES_RECHERCHE")
	public void testFindByIdOk() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);

		final SousTypeProduit parent = new SousTypeProduit(OUTILLAGE, typeProduit);
		parent.setIdSousTypeProduit(10L);

		final Produit produit = new Produit(MARTEAU, parent);
		produit.setIdProduit(100L);

		when(gateway.findById(100L)).thenReturn(produit);

		final OutputDTO dto = service.findById(100L);

		assertThat(dto).isNotNull();
		assertThat(dto.getIdProduit()).isEqualTo(100L);
		assertThat(dto.getProduit()).isEqualTo(MARTEAU);
		assertThat(dto.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(dto.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		verify(gateway, times(1)).findById(100L);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________
	
	
	
	// ========================= TESTS update(...) =========================
	
	
	
	/**
	 * <div>
	 * <p>update(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'interagit avec aucun gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(null) : ExceptionParametreNull + message exact MESSAGE_PARAM_NULL + aucune interaction gateway")
	public void testUpdateNull() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		assertThatThrownBy(() -> service.update(null))
			.isInstanceOf(ExceptionParametreNull.class);

		assertThat(service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>n'interagit avec aucun gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(blank) : ExceptionParametreBlank + message exact MESSAGE_PARAM_BLANK + aucune interaction gateway")
	public void testUpdateBlank() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final InputDTO input = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, ESPACES);

		assertThatThrownBy(() -> service.update(input))
			.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(parent blank) : violation de contrat structurel.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'interagit avec aucun gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(parent blank) : IllegalStateException + message exact MESSAGE_PAS_PARENT + aucune interaction gateway")
	public void testUpdateParentBlank() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final InputDTO input = new ProduitDTO.InputDTO(BAZAR, ESPACES, MARTEAU);

		assertThatThrownBy(() -> service.update(input))
			.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(parent absent) : le parent requis n'existe pas en stockage.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'appelle jamais {@code gateway.update(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(parent absent) : IllegalStateException + message exact MESSAGE_PAS_PARENT + aucune modification gateway")
	public void testUpdateParentAbsent() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final InputDTO input = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);

		when(sousTypeGateway.findByLibelle(OUTILLAGE))
			.thenReturn(Collections.emptyList());

		assertThatThrownBy(() -> service.update(input))
			.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		verify(sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).findAllByParent(any(SousTypeProduit.class));
		verify(gateway, never()).update(org.mockito.ArgumentMatchers.any(Produit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(introuvable) : aucun objet persistant
	 * ne correspond au couple [parent, libellé].</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_OBJ_INTROUVABLE}
	 * + libellé</li>
	 * <li>n'appelle jamais {@code gateway.update(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(introuvable) : null + message exact MESSAGE_OBJ_INTROUVABLE + libellé")
	public void testUpdateIntrouvable() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);

		final SousTypeProduit parentPersistant = new SousTypeProduit(OUTILLAGE, typeProduit);
		parentPersistant.setIdSousTypeProduit(10L);

		final Produit produitScie = new Produit(SCIE, parentPersistant);
		produitScie.setIdProduit(2L);

		final InputDTO input = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);

		when(sousTypeGateway.findByLibelle(OUTILLAGE))
			.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findAllByParent(parentPersistant))
			.thenReturn(Arrays.asList(produitScie));

		final OutputDTO retour = service.update(input);

		assertThat(retour).isNull();
		assertThat(service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_OBJ_INTROUVABLE + MARTEAU);

		verify(sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, never()).update(org.mockito.ArgumentMatchers.any(Produit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(non persistant) : l'objet trouvé n'a pas d'identifiant persistant.</p>
	 * <ul>
	 * <li>lève {@link ExceptionNonPersistant}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_OBJ_NON_PERSISTE}
	 * + libellé</li>
	 * <li>n'appelle jamais {@code gateway.update(...)}.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(non persistant) : ExceptionNonPersistant + message exact MESSAGE_OBJ_NON_PERSISTE + libellé")
	public void testUpdateNonPersistant() throws Exception {

		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);

		final SousTypeProduit parentPersistant = new SousTypeProduit(OUTILLAGE, typeProduit);
		parentPersistant.setIdSousTypeProduit(10L);

		final Produit produitSansId = new Produit(MARTEAU, parentPersistant);

		final InputDTO input = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);

		when(sousTypeGateway.findByLibelle(OUTILLAGE))
			.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findAllByParent(parentPersistant))
			.thenReturn(Arrays.asList(produitSansId));

		assertThatThrownBy(() -> service.update(input))
			.isInstanceOf(ExceptionNonPersistant.class);

		assertThat(service.getMessage())
			.isEqualTo(ProduitICuService.MESSAGE_OBJ_NON_PERSISTE + MARTEAU);

		verify(sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, never()).update(org.mockito.ArgumentMatchers.any(Produit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(ok) : scénario nominal sans stub inutile.</p>
	 * <ul>
	 * <li>retrouve le parent persistant ;</li>
	 * <li>retrouve l'objet persistant par le couple [parent, libellé] ;</li>
	 * <li>conserve l'identifiant persistant ;</li>
	 * <li>délègue exactement une fois au gateway de modification ;</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_MODIF_OK} + parent.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(ok) : OutputDTO cohérent + ID conservé + aucun stub inutile")
	public void testUpdateOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitGatewayIService gateway = mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway = mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service = new ProduitCuService(gateway, sousTypeGateway);

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);

		final SousTypeProduit parentPersistant = new SousTypeProduit(OUTILLAGE, typeProduit);
		parentPersistant.setIdSousTypeProduit(10L);

		final Produit existant = new Produit(MARTEAU, parentPersistant);
		existant.setIdProduit(100L);

		final Produit modifie = new Produit(MARTEAU, parentPersistant);
		modifie.setIdProduit(100L);

		final InputDTO input = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);

		when(sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentPersistant));

		when(gateway.findAllByParent(parentPersistant))
				.thenReturn(Arrays.asList(existant));

		when(gateway.update(any(Produit.class)))
				.thenReturn(modifie);

		final ArgumentCaptor<Produit> captor = ArgumentCaptor.forClass(Produit.class);

		/* ======================= ACT ======================= */
		final OutputDTO retour = service.update(input);

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdProduit()).isEqualTo(100L);
		assertThat(retour.getProduit()).isEqualTo(MARTEAU);
		assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_MODIF_OK + MARTEAU);

		verify(sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, times(1)).update(captor.capture());

		final Produit passeAuGateway = captor.getValue();

		assertThat(passeAuGateway).isNotNull();
		assertThat(passeAuGateway.getIdProduit()).isEqualTo(100L);
		assertThat(passeAuGateway.getProduit()).isEqualTo(MARTEAU);
		assertThat(passeAuGateway.getSousTypeProduit()).isSameAs(parentPersistant);

	} // __________________________________________________________________
	

	
	// ========================= TESTS delete(...) =========================

	
	
	/**
	 * <div>
	 * <p>delete(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'interagit ni avec le Gateway enfant
	 * ni avec le Gateway parent</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(null) : ExceptionParametreNull + message MESSAGE_PARAM_NULL + aucune interaction gateway")
	public void testDeleteNull() {

		final ProduitGatewayIService gateway =
				mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway =
				mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service =
				new ProduitCuService(gateway, sousTypeGateway);

		assertThatThrownBy(() -> service.delete(null))
				.isInstanceOf(ExceptionParametreNull.class);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>n'interagit ni avec le Gateway enfant
	 * ni avec le Gateway parent</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(blank) : ExceptionParametreBlank + message MESSAGE_PARAM_BLANK + aucune interaction gateway")
	public void testDeleteBlank() {

		final ProduitGatewayIService gateway =
				mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway =
				mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service =
				new ProduitCuService(gateway, sousTypeGateway);
		final InputDTO dto =
				new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, ESPACES);

		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(parent blank) : violation de contrat structurel.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'interagit ni avec le Gateway enfant
	 * ni avec le Gateway parent</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(parent blank) : IllegalStateException + message MESSAGE_PAS_PARENT + aucune interaction gateway")
	public void testDeleteParentBlank() {

		final ProduitGatewayIService gateway =
				mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway =
				mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service =
				new ProduitCuService(gateway, sousTypeGateway);
		final InputDTO dto =
				new ProduitDTO.InputDTO(ESPACES, OUTILLAGE, MARTEAU);

		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		verifyNoInteractions(gateway);
		verifyNoInteractions(sousTypeGateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(recherche parent technique KO avec message) :
	 * panne technique pendant la recherche du parent persistant.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#KO_TECHNIQUE_RECHERCHE}
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

		final ProduitGatewayIService gateway =
				mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway =
				mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service =
				new ProduitCuService(gateway, sousTypeGateway);
		final InputDTO dto =
				new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException panneTechnique =
				new IllegalStateException(MESSAGE_GATEWAY);

		when(sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenThrow(panneTechnique);

		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ ProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verifyNoInteractions(gateway);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(parent absent) : le parent requis
	 * n'existe pas en stockage.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_PAS_PARENT}</li>
	 * <li>n'appelle jamais {@code gateway.delete(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(parent absent) : IllegalStateException + message MESSAGE_PAS_PARENT")
	public void testDeleteParentAbsent() throws Exception {

		final ProduitGatewayIService gateway =
				mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway =
				mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service =
				new ProduitCuService(gateway, sousTypeGateway);
		final InputDTO dto =
				new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);

		when(sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Collections.emptyList());

		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		verify(sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).findAllByParent(any(SousTypeProduit.class));
		verify(gateway, never()).delete(any(Produit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(stockage null pendant ré-identification) :
	 * le Gateway retourne {@code null}
	 * pour la liste des enfants du parent persistant.</p>
	 * <ul>
	 * <li>lève {@link ExceptionStockageVide}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_STOCKAGE_NULL}</li>
	 * <li>n'appelle jamais {@code gateway.delete(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(stockage null) : ExceptionStockageVide + message MESSAGE_STOCKAGE_NULL")
	public void testDeleteStockageNull() throws Exception {

		final ProduitGatewayIService gateway =
				mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway =
				mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service =
				new ProduitCuService(gateway, sousTypeGateway);

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);

		final SousTypeProduit parentPersistant =
				new SousTypeProduit(OUTILLAGE, typeProduit);
		parentPersistant.setIdSousTypeProduit(10L);

		final InputDTO dto =
				new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);

		when(sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findAllByParent(parentPersistant))
				.thenReturn(null);

		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionStockageVide.class);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_STOCKAGE_NULL);

		verify(sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, never()).delete(any(Produit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(introuvable) : aucun objet persistant
	 * ne correspond au couple [parent, libellé].</p>
	 * <ul>
	 * <li>ne lève aucune exception</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_OBJ_INTROUVABLE}
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

		final ProduitGatewayIService gateway =
				mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway =
				mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service =
				new ProduitCuService(gateway, sousTypeGateway);

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);

		final SousTypeProduit parentPersistant =
				new SousTypeProduit(OUTILLAGE, typeProduit);
		parentPersistant.setIdSousTypeProduit(10L);

		final Produit produitScie = new Produit(SCIE, parentPersistant);
		produitScie.setIdProduit(2L);

		final InputDTO dto =
				new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);

		when(sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findAllByParent(parentPersistant))
				.thenReturn(Arrays.asList(produitScie));

		service.delete(dto);

		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService.MESSAGE_OBJ_INTROUVABLE + MARTEAU);

		verify(sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, never()).delete(any(Produit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(non persisté) : l'objet ré-identifié
	 * n'a pas d'identifiant persistant.</p>
	 * <ul>
	 * <li>lève {@link ExceptionNonPersistant}</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_OBJ_NON_PERSISTE}
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

		final ProduitGatewayIService gateway =
				mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway =
				mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service =
				new ProduitCuService(gateway, sousTypeGateway);

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);

		final SousTypeProduit parentPersistant =
				new SousTypeProduit(OUTILLAGE, typeProduit);
		parentPersistant.setIdSousTypeProduit(10L);

		final Produit produitSansId = new Produit(MARTEAU, parentPersistant);

		final InputDTO dto =
				new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);

		when(sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findAllByParent(parentPersistant))
				.thenReturn(Arrays.asList(produitSansId));

		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionNonPersistant.class);

		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService.MESSAGE_OBJ_NON_PERSISTE + MARTEAU);

		verify(sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, never()).delete(any(Produit.class));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(KO technique de suppression avec message) :
	 * la cible est correctement ré-identifiée
	 * puis la destruction technique échoue.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_DELETE_KO}
	 * + libellé + tiret + détail technique</li>
	 * <li>appelle exactement une fois {@code gateway.delete(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(KO technique avec message) : exception relancée + message MESSAGE_DELETE_KO + détail technique")
	public void testDeleteTechniqueKoAvecMessage() throws Exception {

		final ProduitGatewayIService gateway =
				mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway =
				mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service =
				new ProduitCuService(gateway, sousTypeGateway);

		final TypeProduit typeProduit = new TypeProduit(BAZAR);
		typeProduit.setIdTypeProduit(1L);

		final SousTypeProduit parentPersistant =
				new SousTypeProduit(OUTILLAGE, typeProduit);
		parentPersistant.setIdSousTypeProduit(10L);

		final Produit cible = new Produit(MARTEAU, parentPersistant);
		cible.setIdProduit(100L);

		final InputDTO dto =
				new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);

		final IllegalStateException ex =
				new IllegalStateException(MESSAGE_GATEWAY);

		when(sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentPersistant));
		when(gateway.findAllByParent(parentPersistant))
				.thenReturn(Arrays.asList(cible));
		doThrow(ex).when(gateway).delete(cible);

		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(ex);

		assertThat(service.getMessage())
				.isEqualTo(
						ProduitICuService.MESSAGE_DELETE_KO
								+ MARTEAU
								+ ProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).findAllByParent(parentPersistant);
		verify(gateway, times(1)).delete(cible);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>delete(ok) : succès nominal complet.</p>
	 * <ul>
	 * <li>ré-identifie d'abord le bon parent persistant
	 * sur le couple [type parent, sous-type parent]</li>
	 * <li>ré-identifie ensuite la bonne cible
	 * sur le couple [parent, libellé]</li>
	 * <li>ne détruit jamais l'homonyme rattaché
	 * à l'autre parent</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_DELETE_OK} + libellé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(ok) : suppression déléguée + message MESSAGE_DELETE_OK + couple [parent, libellé]")
	public void testDeleteOkAvecPreuveCoupleParentLibelle()
			throws Exception {

		final ProduitGatewayIService gateway =
				mock(ProduitGatewayIService.class);
		final SousTypeProduitGatewayIService sousTypeGateway =
				mock(SousTypeProduitGatewayIService.class);
		final ProduitCuService service =
				new ProduitCuService(gateway, sousTypeGateway);

		final TypeProduit typeProduitA = new TypeProduit(BAZAR);
		typeProduitA.setIdTypeProduit(1L);

		final TypeProduit typeProduitB = new TypeProduit(QUINCAILLERIE);
		typeProduitB.setIdTypeProduit(2L);

		final SousTypeProduit parentA =
				new SousTypeProduit(OUTILLAGE, typeProduitA);
		parentA.setIdSousTypeProduit(10L);

		final SousTypeProduit parentB =
				new SousTypeProduit(OUTILLAGE, typeProduitB);
		parentB.setIdSousTypeProduit(20L);

		final Produit homonymeAutreParent =
				new Produit(MARTEAU, parentA);
		homonymeAutreParent.setIdProduit(100L);

		final Produit cible =
				new Produit(MARTEAU, parentB);
		cible.setIdProduit(200L);

		final InputDTO dto =
				new ProduitDTO.InputDTO(
						QUINCAILLERIE,
						OUTILLAGE,
						MARTEAU);

		when(sousTypeGateway.findByLibelle(OUTILLAGE))
				.thenReturn(Arrays.asList(parentA, parentB));
		when(gateway.findAllByParent(parentB))
				.thenReturn(Arrays.asList(cible));

		service.delete(dto);

		assertThat(service.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_DELETE_OK + MARTEAU);

		verify(sousTypeGateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, never()).findAllByParent(parentA);
		verify(gateway, times(1)).findAllByParent(parentB);
		verify(gateway, never()).delete(homonymeAutreParent);
		verify(gateway, times(1)).delete(cible);

	} // __________________________________________________________________	


	
	// ============================ TESTS count() ==========================
	
	

	// ========================= TESTS getMessage() =======================
	
	
	
}
