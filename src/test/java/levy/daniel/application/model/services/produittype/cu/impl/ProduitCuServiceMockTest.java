package levy.daniel.application.model.services.produittype.cu.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import levy.daniel.application.model.dto.produittype.ProduitDTO;
import levy.daniel.application.model.dto.produittype.ProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.ProduitDTO.OutputDTO;
import levy.daniel.application.model.metier.produittype.Produit;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.TypeProduit;
import levy.daniel.application.model.services.produittype.cu.ProduitICuService;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionDoublon;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionParametreBlank;
import levy.daniel.application.model.services.produittype.gateway.ProduitGatewayIService;
import levy.daniel.application.model.services.produittype.gateway.SousTypeProduitGatewayIService;

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

	
	
		
}
