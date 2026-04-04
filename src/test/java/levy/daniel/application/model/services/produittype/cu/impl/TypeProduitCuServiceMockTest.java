package levy.daniel.application.model.services.produittype.cu.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
	
	/**
	 * "lecture technique KO"
	 */
	public static final String LECTURE_TECHNIQUE_KO = "lecture technique KO";

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

	// ---------------------- Creer(...) -------------------------------//
	
	/**
	 * <div>
	 * <p>creer(null) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link TypeProduitICuService#MESSAGE_CREER_NULL}</li>
	 * <li>n'interagit jamais avec le Gateway</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		/* ======================= ACT ======================= */
		final OutputDTO retour = service.creer(null);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_CREER_NULL);

		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>creer(blank) : violation de contrat applicatif.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne {@link TypeProduitICuService#MESSAGE_CREER_NOM_BLANK}</li>
	 * <li>n'interagit jamais avec le Gateway</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(blank) : ExceptionParametreBlank + message MESSAGE_CREER_NOM_BLANK + aucune interaction gateway")
	public void testCreerBlank() {

		/* ===================== ARRANGE ===================== */
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final InputDTO dto = new TypeProduitDTO.InputDTO(ESPACES);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_CREER_NOM_BLANK);

		verifyNoInteractions(gateway);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>creer(doublon) : refus métier d'unicité.</p>
	 * <ul>
	 * <li>lève {@link ExceptionDoublon}</li>
	 * <li>positionne {@link TypeProduitICuService#MESSAGE_DOUBLON} + libellé</li>
	 * <li>ne délègue jamais {@code gateway.creer(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(doublon) : ExceptionDoublon + message MESSAGE_DOUBLON + libellé + aucune création gateway")
	public void testCreerDoublon() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final InputDTO dto = new TypeProduitDTO.InputDTO(VETEMENT);

		/* doublon si gateway.findByLibelle(libelle) != null. */
		when(gateway.findByLibelle(VETEMENT)).thenReturn(new TypeProduit(VETEMENT));

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(ExceptionDoublon.class);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_DOUBLON + VETEMENT);

		verify(gateway, times(1)).findByLibelle(VETEMENT);
		verify(gateway, never()).creer(any(TypeProduit.class));
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>creer(contrôle technique KO avec message) :</p>
	 * <ul>
	 * <li>l'exception technique du contrôle d'unicité est propagée</li>
	 * <li>le message utilisateur est rationalisé avec
	 * {@link TypeProduitICuService#PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER}</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final InputDTO dto = new TypeProduitDTO.InputDTO(BAZAR);

		final IllegalStateException panneTechnique
			= new IllegalStateException(LECTURE_TECHNIQUE_KO);

		when(gateway.findByLibelle(BAZAR)).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER
						+ LECTURE_TECHNIQUE_KO);

		verify(gateway, times(1)).findByLibelle(BAZAR);
		verify(gateway, never()).creer(any(TypeProduit.class));
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>creer(contrôle technique KO sans message) :</p>
	 * <ul>
	 * <li>l'exception technique du contrôle d'unicité est propagée</li>
	 * <li>le message utilisateur retombe sur
	 * {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final InputDTO dto = new TypeProduitDTO.InputDTO(TOURISME);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.findByLibelle(TOURISME)).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.PREFIX_MESSAGE_CONTROLE_TECHNIQUE_CREER
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findByLibelle(TOURISME);
		verify(gateway, never()).creer(any(TypeProduit.class));
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>creer(création technique KO avec message) :</p>
	 * <ul>
	 * <li>l'exception du Gateway est propagée</li>
	 * <li>le message utilisateur est rationalisé avec
	 * {@link TypeProduitICuService#PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER}</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final InputDTO dto = new TypeProduitDTO.InputDTO(OUTILLAGE);

		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(null);

		final IllegalStateException panneTechnique
			= new IllegalStateException("écriture technique KO");

		when(gateway.creer(any(TypeProduit.class))).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER
						+ "écriture technique KO");

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(any(TypeProduit.class));
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>creer(création technique KO sans message) :</p>
	 * <ul>
	 * <li>l'exception du Gateway est propagée</li>
	 * <li>le message utilisateur retombe sur
	 * {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final InputDTO dto = new TypeProduitDTO.InputDTO(OUTILLAGE);

		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(null);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.creer(any(TypeProduit.class))).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.PREFIX_MESSAGE_CREATION_TECHNIQUE_CREER
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(any(TypeProduit.class));
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>creer(gateway retourne null) :</p>
	 * <ul>
	 * <li>sécurise le contrat observable du CU</li>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne
	 * {@link TypeProduitICuService#MESSAGE_CREATION_TECHNIQUE_KO_CREER}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(gateway retourne null) : IllegalStateException + message MESSAGE_CREATION_TECHNIQUE_KO_CREER")
	public void testCreerGatewayRetourneNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final InputDTO dto = new TypeProduitDTO.InputDTO(OUTILLAGE);

		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(null);
		when(gateway.creer(any(TypeProduit.class))).thenReturn(null);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.creer(dto))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(TypeProduitICuService.MESSAGE_CREATION_TECHNIQUE_KO_CREER);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_CREATION_TECHNIQUE_KO_CREER);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(any(TypeProduit.class));
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>creer(ok) : scénario nominal complet.</p>
	 * <ul>
	 * <li>contrôle d'unicité</li>
	 * <li>délégation à {@code gateway.creer(...)}</li>
	 * <li>retour d'un {@link OutputDTO}</li>
	 * <li>message final
	 * {@link TypeProduitICuService#MESSAGE_CREER_OK}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(ok) : délégation gateway.creer + OutputDTO + message MESSAGE_CREER_OK")
	public void testCreerOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final InputDTO dto = new TypeProduitDTO.InputDTO(OUTILLAGE);

		/* pas doublon => findByLibelle retourne null. */
		when(gateway.findByLibelle(OUTILLAGE)).thenReturn(null);

		final TypeProduit cree = new TypeProduit(OUTILLAGE);
		cree.setIdTypeProduit(1L);

		when(gateway.creer(any(TypeProduit.class))).thenReturn(cree);

		/* ======================= ACT ======================= */
		final OutputDTO retour = service.creer(dto);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdTypeProduit()).isEqualTo(1L);
		assertThat(retour.getTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_CREER_OK);

		final ArgumentCaptor<TypeProduit> captor = ArgumentCaptor.forClass(TypeProduit.class);

		verify(gateway, times(1)).findByLibelle(OUTILLAGE);
		verify(gateway, times(1)).creer(captor.capture());

		final TypeProduit envoye = captor.getValue();

		assertThat(envoye).isNotNull();
		assertThat(envoye.getIdTypeProduit()).isNull();
		assertThat(envoye.getTypeProduit()).isEqualTo(OUTILLAGE);
		
	} // __________________________________________________________________

	

	// -------------------- Rechercher(...) -----------------------------//

	

	/**
	 * <div>
	 * <p>rechercherTous() : stockage null.</p>
	 * <ul>
	 * <li>lève {@link ExceptionStockageVide}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_STOCKAGE_NULL}</li>
	 * <li>délègue une seule fois à {@code gateway.rechercherTous()}</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		when(gateway.rechercherTous()).thenReturn(null);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTous())
				.isInstanceOf(ExceptionStockageVide.class)
				.hasMessage(TypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		verify(gateway, times(1)).rechercherTous();
		
	} // __________________________________________________________________

	

	/**
	 * <div>
	 * <p>rechercherTous() : recherche technique KO avec message.</p>
	 * <ul>
	 * <li>propage l'exception du Gateway</li>
	 * <li>rationalise le message utilisateur avec
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTous() : gateway KO avec message -> propage l'exception + message technique rationalisé")
	public void testRechercherTousTechniqueKoAvecMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);

		when(gateway.rechercherTous()).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTous())
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ TypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		verify(gateway, times(1)).rechercherTous();
		
	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>rechercherTous() : recherche technique KO sans message.</p>
	 * <ul>
	 * <li>propage l'exception du Gateway</li>
	 * <li>utilise le fallback
	 * {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTous() : gateway KO sans message -> fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testRechercherTousTechniqueKoSansMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.rechercherTous()).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTous())
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).rechercherTous();
		
	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTous() : résultats vides après filtrage.</p>
	 * <ul>
	 * <li>retourne une liste non {@code null}</li>
	 * <li>retourne une liste vide</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final List<TypeProduit> records = new ArrayList<>();
		records.add(null);

		when(gateway.rechercherTous()).thenReturn(records);

		/* ======================= ACT ======================= */
		final List<TypeProduitDTO.OutputDTO> retour = service.rechercherTous();
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(gateway, times(1)).rechercherTous();
		
	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>rechercherTous() : scénario nominal complet.</p>
	 * <ul>
	 * <li>filtre les {@code null}</li>
	 * <li>trie les objets métier</li>
	 * <li>dédoublonne la réponse DTO</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final TypeProduit tpTourisme = new TypeProduit(TOURISME);
		tpTourisme.setIdTypeProduit(2L);

		final TypeProduit tpBazar = new TypeProduit(BAZAR);
		tpBazar.setIdTypeProduit(1L);

		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(tpTourisme, null, tpBazar, tpTourisme));

		/* ======================= ACT ======================= */
		final List<TypeProduitDTO.OutputDTO> retour = service.rechercherTous();
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);

		assertThat(retour)
				.extracting(TypeProduitDTO.OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, TOURISME);

		assertThat(retour)
				.extracting(TypeProduitDTO.OutputDTO::getIdTypeProduit)
				.containsExactly(1L, 2L);

		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(gateway, times(1)).rechercherTous();
		
	} // __________________________________________________________________
	/**
	 * <div>
	 * <p>rechercherTousString() : stockage null.</p>
	 * <ul>
	 * <li>lève {@link ExceptionStockageVide}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_STOCKAGE_NULL}</li>
	 * <li>délègue une seule fois à {@code gateway.rechercherTous()}</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		when(gateway.rechercherTous()).thenReturn(null);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isInstanceOf(ExceptionStockageVide.class)
				.hasMessage(TypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		verify(gateway, times(1)).rechercherTous();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousString() : recherche technique KO avec message.</p>
	 * <ul>
	 * <li>propage l'exception du Gateway</li>
	 * <li>rationalise le message utilisateur avec
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousString() : gateway KO avec message -> propage l'exception + message technique rationalisé")
	public void testRechercherTousStringTechniqueKoAvecMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY_BIS);

		when(gateway.rechercherTous()).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ TypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY_BIS);

		verify(gateway, times(1)).rechercherTous();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousString() : recherche technique KO sans message.</p>
	 * <ul>
	 * <li>propage l'exception du Gateway</li>
	 * <li>utilise le fallback
	 * {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousString() : gateway KO sans message -> fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testRechercherTousStringTechniqueKoSansMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.rechercherTous()).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.rechercherTousString())
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).rechercherTous();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousString() : résultats vides après filtrage.</p>
	 * <ul>
	 * <li>retourne une liste non {@code null}</li>
	 * <li>retourne une liste vide</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final List<TypeProduit> records = new ArrayList<>();
		records.add(null);

		when(gateway.rechercherTous()).thenReturn(records);

		/* ======================= ACT ======================= */
		final List<String> retour = service.rechercherTousString();
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(gateway, times(1)).rechercherTous();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousString() : scénario nominal complet.</p>
	 * <ul>
	 * <li>filtre les {@code null}</li>
	 * <li>trie les objets métier</li>
	 * <li>dédoublonne les libellés</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousString() : filtre nulls + trie + dédoublonne + message MESSAGE_RECHERCHE_OK")
	public void testRechercherTousStringOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final TypeProduit tpTourisme = new TypeProduit(TOURISME);
		tpTourisme.setIdTypeProduit(2L);

		final TypeProduit tpBazar = new TypeProduit(BAZAR);
		tpBazar.setIdTypeProduit(1L);

		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(tpTourisme, null, tpBazar, tpTourisme));

		/* ======================= ACT ======================= */
		final List<String> retour = service.rechercherTousString();
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).containsExactly(BAZAR, TOURISME);
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(gateway, times(1)).rechercherTous();

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>rechercherTousParPage(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PAGEABLE_NULL}</li>
	 * <li>n'interagit jamais avec le Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousParPage(null) : IllegalStateException + message MESSAGE_PAGEABLE_NULL + aucune interaction gateway")
	public void testRechercherTousParPageNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(
				() -> service.rechercherTousParPage(null))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(TypeProduitICuService.MESSAGE_PAGEABLE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PAGEABLE_NULL);

		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousParPage() : recherche technique KO avec message.</p>
	 * <ul>
	 * <li>propage l'exception du Gateway</li>
	 * <li>rationalise le message utilisateur avec
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final levy.daniel.application.model.services.produittype.pagination.RequetePage requete
				= new levy.daniel.application.model.services.produittype.pagination.RequetePage(0, 2);

		final IllegalStateException panneTechnique
				= new IllegalStateException(MESSAGE_GATEWAY);

		when(gateway.rechercherTousParPage(requete)).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(
				() -> service.rechercherTousParPage(requete))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ TypeProduitICuService.TIRET_ESPACE
						+ MESSAGE_GATEWAY);

		verify(gateway, times(1)).rechercherTousParPage(requete);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousParPage() : recherche technique KO sans message.</p>
	 * <ul>
	 * <li>propage l'exception du Gateway</li>
	 * <li>utilise le fallback
	 * {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final levy.daniel.application.model.services.produittype.pagination.RequetePage requete
				= new levy.daniel.application.model.services.produittype.pagination.RequetePage(0, 2);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.rechercherTousParPage(requete)).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(
				() -> service.rechercherTousParPage(requete))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).rechercherTousParPage(requete);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousParPage() : résultat paginé null.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_PAGINEE_KO}</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final levy.daniel.application.model.services.produittype.pagination.RequetePage requete
				= new levy.daniel.application.model.services.produittype.pagination.RequetePage(0, 2);

		when(gateway.rechercherTousParPage(requete)).thenReturn(null);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(
				() -> service.rechercherTousParPage(requete))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(TypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_KO);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_KO);

		verify(gateway, times(1)).rechercherTousParPage(requete);

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
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_PAGINEE_OK}</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final levy.daniel.application.model.services.produittype.pagination.RequetePage requete
				= new levy.daniel.application.model.services.produittype.pagination.RequetePage(0, 4);

		final TypeProduit tpTourisme = new TypeProduit(TOURISME);
		tpTourisme.setIdTypeProduit(2L);

		final TypeProduit tpBazar = new TypeProduit(BAZAR);
		tpBazar.setIdTypeProduit(1L);

		final levy.daniel.application.model.services.produittype.pagination.ResultatPage<TypeProduit> resultatGateway
				= new levy.daniel.application.model.services.produittype.pagination.ResultatPage<TypeProduit>(
						Arrays.asList(tpTourisme, null, tpBazar, tpTourisme),
						0,
						4,
						10L);

		when(gateway.rechercherTousParPage(requete)).thenReturn(resultatGateway);

		/* ======================= ACT ======================= */
		final levy.daniel.application.model.services.produittype.pagination.ResultatPage<OutputDTO> retour
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
				.extracting(OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, TOURISME);

		assertThat(retour.getContent())
				.extracting(OutputDTO::getIdTypeProduit)
				.containsExactly(1L, 2L);

		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		verify(gateway, times(1)).rechercherTousParPage(requete);

	} // __________________________________________________________________	
	

	
	/**
	 * <div>
	 * <p>findByLibelle(blank) : retourne {@code null} et message exact.</p>
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

		// ======================= ACT =======================

		final OutputDTO retour = service.findByLibelle(ESPACES);
		final String message = service.getMessage();

		// ===================== ASSERT ======================

		assertThat(retour).isNull();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(introuvable) : retourne {@code null}
	 * et message exact "introuvable".</p>
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

		final String libelleAbsent = "IT_FIND_BY_LIBELLE_ABSENT_01";

		when(gateway.findByLibelle(libelleAbsent)).thenReturn(null);

		// ======================= ACT =======================

		final OutputDTO retour = service.findByLibelle(libelleAbsent);
		final String message = service.getMessage();

		// ===================== ASSERT ======================

		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelleAbsent);

		verify(gateway, times(1)).findByLibelle(libelleAbsent);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(KO technique avec message) :
	 * propage l'exception et rationalise le message utilisateur.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(KO technique avec message) : propage l'exception + message technique rationalisé")
	public void testFindByLibelleTechniqueKoAvecMessage() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final String libelle = "IT_FIND_BY_LIBELLE_TECH_KO_01";
		final IllegalStateException panneTechnique
				= new IllegalStateException(LECTURE_TECHNIQUE_KO);

		when(gateway.findByLibelle(libelle)).thenThrow(panneTechnique);

		// =================== ACT & ASSERT ==================

		assertThatThrownBy(() -> service.findByLibelle(libelle))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ TypeProduitICuService.TIRET_ESPACE
						+ LECTURE_TECHNIQUE_KO);

		verify(gateway, times(1)).findByLibelle(libelle);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(KO technique sans message) :
	 * fallback sur MSG_ERREUR_NON_SPECIFIEE.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testFindByLibelleTechniqueKoSansMessage() throws Exception {

		// ===================== ARRANGE =====================

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final String libelle = "IT_FIND_BY_LIBELLE_TECH_KO_02";
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.findByLibelle(libelle)).thenThrow(panneTechnique);

		// =================== ACT & ASSERT ==================

		assertThatThrownBy(() -> service.findByLibelle(libelle))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findByLibelle(libelle);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(ok) : retourne un DTO cohérent
	 * et positionne le message exact de succès.</p>
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

		final String libelle = "IT_FIND_BY_LIBELLE_OK_01";
		final TypeProduit tp = new TypeProduit(libelle);
		tp.setIdTypeProduit(7L);

		when(gateway.findByLibelle(libelle)).thenReturn(tp);

		// ======================= ACT =======================

		final OutputDTO retour = service.findByLibelle(libelle);
		final String message = service.getMessage();

		// ===================== ASSERT ======================

		assertThat(retour).isNotNull();
		assertThat(retour.getIdTypeProduit()).isEqualTo(7L);
		assertThat(retour.getTypeProduit()).isEqualTo(libelle);
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		verify(gateway, times(1)).findByLibelle(libelle);

	} // __________________________________________________________________	
	

	
	/**
	 * <div>
	 * <p>findByLibelleRapide(null) : émet MESSAGE_PARAM_NULL + LOG
	 * + IllegalStateException.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'interagit jamais avec le Gateway</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.findByLibelleRapide(null))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage(TypeProduitICuService.MESSAGE_PARAM_NULL);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Si pContenu est blank : délègue à rechercherTous().</p>
	 * <ul>
	 * <li>n'appelle jamais {@code gateway.findByLibelleRapide(...)}</li>
	 * <li>appelle {@code gateway.rechercherTous()}</li>
	 * <li>retourne la liste DTO issue de la recherche exhaustive</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final TypeProduit tpTourisme = new TypeProduit(TOURISME);
		tpTourisme.setIdTypeProduit(2L);

		final TypeProduit tpBazar = new TypeProduit(BAZAR);
		tpBazar.setIdTypeProduit(1L);

		when(gateway.rechercherTous())
				.thenReturn(Arrays.asList(tpTourisme, null, tpBazar, tpTourisme));

		/* ======================= ACT ======================= */
		final List<OutputDTO> retour = service.findByLibelleRapide(ESPACES);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);

		assertThat(retour)
				.extracting(OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, TOURISME);

		assertThat(retour)
				.extracting(OutputDTO::getIdTypeProduit)
				.containsExactly(1L, 2L);

		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(gateway, times(1)).rechercherTous();
		verify(gateway, never()).findByLibelleRapide(anyString());

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Si le Gateway lève une exception avec message :
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final String contenu = "QA_FIND_RAPIDE_TECH_01";
		final IllegalStateException panneTechnique
				= new IllegalStateException(LECTURE_TECHNIQUE_KO);

		when(gateway.findByLibelleRapide(contenu)).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.findByLibelleRapide(contenu))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ TypeProduitICuService.TIRET_ESPACE
						+ LECTURE_TECHNIQUE_KO);

		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Si le Gateway lève une exception sans message :
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final String contenu = "QA_FIND_RAPIDE_TECH_02";
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.findByLibelleRapide(contenu)).thenThrow(panneTechnique);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.findByLibelleRapide(contenu))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
						+ TypeProduitICuService.TIRET_ESPACE
						+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>Si le Gateway retourne null :
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final String contenu = "QA_FIND_RAPIDE_NULL";

		when(gateway.findByLibelleRapide(contenu)).thenReturn(null);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> service.findByLibelleRapide(contenu))
				.isInstanceOf(ExceptionStockageVide.class)
				.hasMessage(TypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_STOCKAGE_NULL);

		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();

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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final String contenu = "QA_FIND_RAPIDE_VIDE";
		final List<TypeProduit> records = new ArrayList<TypeProduit>();
		records.add(null);

		when(gateway.findByLibelleRapide(contenu)).thenReturn(records);

		/* ======================= ACT ======================= */
		final List<OutputDTO> retour = service.findByLibelleRapide(contenu);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();

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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final String contenu = "QA_FIND_RAPIDE_OK";

		final TypeProduit tpTourisme = new TypeProduit(TOURISME);
		tpTourisme.setIdTypeProduit(2L);

		final TypeProduit tpBazar = new TypeProduit(BAZAR);
		tpBazar.setIdTypeProduit(1L);

		when(gateway.findByLibelleRapide(contenu))
				.thenReturn(Arrays.asList(tpTourisme, null, tpBazar, tpTourisme));

		/* ======================= ACT ======================= */
		final List<OutputDTO> retour = service.findByLibelleRapide(contenu);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);

		assertThat(retour)
				.extracting(OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, TOURISME);

		assertThat(retour)
				.extracting(OutputDTO::getIdTypeProduit)
				.containsExactly(1L, 2L);

		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(gateway, times(1)).findByLibelleRapide(contenu);
		verify(gateway, never()).rechercherTous();

	} // __________________________________________________________________	
	

	
	/**
	 * <div>
	 * <p>findByDTO(null) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OBJ_NULL}</li>
	 * <li>n'appelle pas le Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(null) : null + message MESSAGE_RECHERCHE_OBJ_NULL")
	public void testFindByDTONull() throws Exception {

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final OutputDTO retour = service.findByDTO(null);
		final String message = service.getMessage();

		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OBJ_NULL);

		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>findByDTO(blank) : délégation exacte au scénario blank
	 * de findByLibelle(...).</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>n'appelle pas le Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(blank) : null + message MESSAGE_PARAM_BLANK + aucune interaction gateway")
	public void testFindByDTOBlank() throws Exception {

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final InputDTO dto = new TypeProduitDTO.InputDTO(ESPACES);

		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>findByDTO(introuvable) : délégation exacte au scénario
	 * d'introuvabilité de findByLibelle(...).</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
	 * <li>délègue une fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(introuvable) : null + message MESSAGE_OBJ_INTROUVABLE + libellé")
	public void testFindByDTOIntrouvable() throws Exception {

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelleAbsent = "IT_FIND_BY_DTO_ABSENT_01";
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelleAbsent);

		when(gateway.findByLibelle(libelleAbsent)).thenReturn(null);

		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelleAbsent);

		verify(gateway, times(1)).findByLibelle(libelleAbsent);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>findByDTO(KO technique avec message) : délégation exacte
	 * au scénario technique de findByLibelle(...).</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>rationalise le message utilisateur</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(KO technique avec message) : propage l'exception + message technique rationalisé")
	public void testFindByDTOTechniqueKoAvecMessage() throws Exception {

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = "IT_FIND_BY_DTO_TECH_KO_01";
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final IllegalStateException panneTechnique =
				new IllegalStateException(LECTURE_TECHNIQUE_KO);

		when(gateway.findByLibelle(libelle)).thenThrow(panneTechnique);

		assertThatThrownBy(() -> service.findByDTO(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ TypeProduitICuService.TIRET_ESPACE
								+ LECTURE_TECHNIQUE_KO);

		verify(gateway, times(1)).findByLibelle(libelle);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>findByDTO(KO technique sans message) : délégation exacte
	 * au scénario technique sans message de findByLibelle(...).</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testFindByDTOTechniqueKoSansMessage() throws Exception {

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = "IT_FIND_BY_DTO_TECH_KO_02";
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.findByLibelle(libelle)).thenThrow(panneTechnique);

		assertThatThrownBy(() -> service.findByDTO(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ TypeProduitICuService.TIRET_ESPACE
								+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findByLibelle(libelle);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>findByDTO(ok) : délégation exacte au scénario nominal
	 * de findByLibelle(...).</p>
	 * <ul>
	 * <li>retourne un OutputDTO cohérent</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}</li>
	 * <li>délègue une fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(ok) : retourne OutputDTO cohérent + message exact de succès")
	public void testFindByDTOOk() throws Exception {

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final InputDTO dto = new TypeProduitDTO.InputDTO(TOURISME);
		final TypeProduit tp = new TypeProduit(TOURISME);

		tp.setIdTypeProduit(9L);

		when(gateway.findByLibelle(TOURISME)).thenReturn(tp);

		final OutputDTO retour = service.findByDTO(dto);
		final String message = service.getMessage();

		assertThat(retour).isNotNull();
		assertThat(retour.getIdTypeProduit()).isEqualTo(9L);
		assertThat(retour.getTypeProduit()).isEqualTo(TOURISME);
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		verify(gateway, times(1)).findByLibelle(TOURISME);

	} // __________________________________________________________________	
	

	
	/**
	 * <div>
	 * <p>findById(null) : erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_NULL}</li>
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

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final OutputDTO retour = service.findById(null);
		final String message = service.getMessage();

		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>findById(introuvable) : cas nominal de non-trouvabilité.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + id</li>
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

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final Long id = 12L;

		when(gateway.findById(id)).thenReturn(null);

		final OutputDTO retour = service.findById(id);
		final String message = service.getMessage();

		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + id);

		verify(gateway, times(1)).findById(id);

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
	@DisplayName("findById(KO technique avec message) : propage l'exception + message technique rationalisé")
	public void testFindByIdTechniqueKoAvecMessage() throws Exception {

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final Long id = 21L;
		final IllegalStateException panneTechnique =
				new IllegalStateException(LECTURE_TECHNIQUE_KO);

		when(gateway.findById(id)).thenThrow(panneTechnique);

		assertThatThrownBy(() -> service.findById(id))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ TypeProduitICuService.TIRET_ESPACE
								+ LECTURE_TECHNIQUE_KO);

		verify(gateway, times(1)).findById(id);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>findById(KO technique sans message) : panne technique sans message
	 * remontée par le Gateway.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>délègue une fois au Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testFindByIdTechniqueKoSansMessage() throws Exception {

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final Long id = 22L;
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.findById(id)).thenThrow(panneTechnique);

		assertThatThrownBy(() -> service.findById(id))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ TypeProduitICuService.TIRET_ESPACE
								+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findById(id);

	} // __________________________________________________________________
	
	
	
	/**
	 * <div>
	 * <p>findById(ok) : succès nominal de recherche par identifiant.</p>
	 * <ul>
	 * <li>retourne un OutputDTO cohérent</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_SUCCES_RECHERCHE}</li>
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

		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		final Long id = 3L;
		final TypeProduit tp = new TypeProduit(BAZAR);
		tp.setIdTypeProduit(id);

		when(gateway.findById(id)).thenReturn(tp);

		final OutputDTO retour = service.findById(id);
		final String message = service.getMessage();

		assertThat(retour).isNotNull();
		assertThat(retour.getIdTypeProduit()).isEqualTo(id);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		verify(gateway, times(1)).findById(id);

	} // __________________________________________________________________	

	

	// ------------------------- update(...) -----------------------------//



	/**
	 * <div>
	 * <p>update(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'interagit jamais avec le Gateway</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(null) : ExceptionParametreNull + message MESSAGE_PARAM_NULL + aucune interaction gateway")
	public void testUpdateNull() {

		/* ===================== ARRANGE ===================== */
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(null))
				.isInstanceOf(ExceptionParametreNull.class);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>n'interagit jamais avec le Gateway</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(blank) : ExceptionParametreBlank + message MESSAGE_PARAM_BLANK + aucune interaction gateway")
	public void testUpdateBlank() {

		/* ===================== ARRANGE ===================== */
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final InputDTO dto = new TypeProduitDTO.InputDTO(ESPACES);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionParametreBlank.class);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(recherche technique KO avec message) :
	 * panne technique pendant la ré-identification
	 * par libellé exact.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>rationalise le message utilisateur
	 * avec {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}</li>
	 * <li>n'appelle jamais {@code gateway.update(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(recherche KO technique avec message) : propage l'exception + message KO_TECHNIQUE_RECHERCHE")
	public void testUpdateRechercheTechniqueKoAvecMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = TOURISME;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final IllegalStateException panneTechnique =
				new IllegalStateException(LECTURE_TECHNIQUE_KO);

		when(gateway.findByLibelle(libelle)).thenThrow(panneTechnique);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ TypeProduitICuService.TIRET_ESPACE
								+ LECTURE_TECHNIQUE_KO);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, never()).update(any(TypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(recherche technique KO sans message) :
	 * panne technique sans message pendant
	 * la ré-identification par libellé exact.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>n'appelle jamais {@code gateway.update(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(recherche KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testUpdateRechercheTechniqueKoSansMessage() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = OUTILLAGE;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.findByLibelle(libelle)).thenThrow(panneTechnique);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ TypeProduitICuService.TIRET_ESPACE
								+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, never()).update(any(TypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(introuvable) : aucun objet persistant
	 * ne correspond au libellé exact.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = VETEMENT;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);

		when(gateway.findByLibelle(libelle)).thenReturn(null);

		/* ======================= ACT ======================= */
		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelle);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, never()).update(any(TypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(non persistant) : l'objet ré-identifié
	 * existe mais ne porte pas d'identifiant persistant.</p>
	 * <ul>
	 * <li>lève {@link ExceptionNonPersistant}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_NON_PERSISTE} + libellé</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = TOURISME;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final TypeProduit existant = new TypeProduit(libelle);

		/* id null => non persistant. */
		existant.setIdTypeProduit(null);

		when(gateway.findByLibelle(libelle)).thenReturn(existant);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(ExceptionNonPersistant.class);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE + libelle);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, never()).update(any(TypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(modification technique KO avec message) :
	 * le Gateway échoue pendant la délégation de modification.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_MODIF_KO} + libellé
	 * + tiret + message technique</li>
	 * <li>prouve la réinjection de l'ID persistant
	 * dans l'objet envoyé au Gateway</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = BAZAR;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final TypeProduit existant = new TypeProduit(libelle);
		existant.setIdTypeProduit(41L);

		final IllegalStateException panneTechnique =
				new IllegalStateException(MESSAGE_GATEWAY);

		when(gateway.findByLibelle(libelle)).thenReturn(existant);
		when(gateway.update(any(TypeProduit.class))).thenThrow(panneTechnique);

		final ArgumentCaptor<TypeProduit> captor =
				ArgumentCaptor.forClass(TypeProduit.class);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_MODIF_KO
								+ libelle
								+ TypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).update(captor.capture());

		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdTypeProduit()).isEqualTo(41L);
		assertThat(captor.getValue().getTypeProduit()).isEqualTo(libelle);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(modification technique KO sans message) :
	 * le Gateway échoue sans message
	 * pendant la délégation de modification.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = OUTILLAGE;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final TypeProduit existant = new TypeProduit(libelle);
		existant.setIdTypeProduit(42L);

		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.findByLibelle(libelle)).thenReturn(existant);
		when(gateway.update(any(TypeProduit.class))).thenThrow(panneTechnique);

		final ArgumentCaptor<TypeProduit> captor =
				ArgumentCaptor.forClass(TypeProduit.class);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_MODIF_KO
								+ libelle
								+ TypeProduitICuService.TIRET_ESPACE
								+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).update(captor.capture());

		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdTypeProduit()).isEqualTo(42L);
		assertThat(captor.getValue().getTypeProduit()).isEqualTo(libelle);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(gateway retourne null) :
	 * le Gateway accepte l'appel de modification
	 * mais ne retourne aucun objet modifié.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_MODIF_KO} + libellé</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = OUTILLAGE;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final TypeProduit existant = new TypeProduit(libelle);
		existant.setIdTypeProduit(10L);

		when(gateway.findByLibelle(libelle)).thenReturn(existant);
		when(gateway.update(any(TypeProduit.class))).thenReturn(null);

		final ArgumentCaptor<TypeProduit> captor =
				ArgumentCaptor.forClass(TypeProduit.class);

		/* ======================= ACT ======================= */
		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_MODIF_KO + libelle);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).update(captor.capture());

		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdTypeProduit()).isEqualTo(10L);
		assertThat(captor.getValue().getTypeProduit()).isEqualTo(libelle);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(objet modifié non persistant) :
	 * le Gateway retourne un objet modifié
	 * dont l'identifiant est redevenu {@code null}.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_NON_PERSISTE} + libellé</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = TOURISME;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);

		final TypeProduit existant = new TypeProduit(libelle);
		existant.setIdTypeProduit(77L);

		final TypeProduit modifie = new TypeProduit(libelle);
		modifie.setIdTypeProduit(null);

		when(gateway.findByLibelle(libelle)).thenReturn(existant);
		when(gateway.update(any(TypeProduit.class))).thenReturn(modifie);

		final ArgumentCaptor<TypeProduit> captor =
				ArgumentCaptor.forClass(TypeProduit.class);

		/* ===================== ACT & ASSERT ===================== */
		assertThatThrownBy(() -> service.update(dto))
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE + libelle);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).update(captor.capture());

		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdTypeProduit()).isEqualTo(77L);
		assertThat(captor.getValue().getTypeProduit()).isEqualTo(libelle);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(ok) : succès nominal complet.</p>
	 * <ul>
	 * <li>retourne un {@link OutputDTO} cohérent</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_MODIF_OK} + libellé</li>
	 * <li>prouve la réinjection de l'ID persistant
	 * dans l'objet envoyé au Gateway</li>
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
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = BAZAR;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);

		final TypeProduit existant = new TypeProduit(libelle);
		existant.setIdTypeProduit(5L);

		final TypeProduit modifie = new TypeProduit(libelle);
		modifie.setIdTypeProduit(5L);

		when(gateway.findByLibelle(libelle)).thenReturn(existant);
		when(gateway.update(any(TypeProduit.class))).thenReturn(modifie);

		final ArgumentCaptor<TypeProduit> captor =
				ArgumentCaptor.forClass(TypeProduit.class);

		/* ======================= ACT ======================= */
		final OutputDTO retour = service.update(dto);
		final String message = service.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdTypeProduit()).isEqualTo(5L);
		assertThat(retour.getTypeProduit()).isEqualTo(libelle);
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_MODIF_OK + libelle);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).update(captor.capture());

		assertThat(captor.getValue()).isNotNull();
		assertThat(captor.getValue().getIdTypeProduit()).isEqualTo(5L);
		assertThat(captor.getValue().getTypeProduit()).isEqualTo(libelle);

	} // __________________________________________________________________
	

	
	// ---------------------- delete(...) -------------------------------//

	
	
	/**
	 * <div>
	 * <p>delete(null) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreNull}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_NULL}</li>
	 * <li>n'interagit jamais avec le Gateway</li>
	 * </ul>
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

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_NULL);

		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(blank) : violation de contrat.</p>
	 * <ul>
	 * <li>lève {@link ExceptionParametreBlank}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_PARAM_BLANK}</li>
	 * <li>n'interagit jamais avec le Gateway</li>
	 * </ul>
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

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(recherche KO technique avec message) :
	 * panne technique pendant la ré-identification
	 * par libellé exact.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>rationalise le message utilisateur
	 * avec {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}</li>
	 * <li>n'appelle jamais {@code gateway.delete(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(recherche KO technique avec message) : propage l'exception + message KO_TECHNIQUE_RECHERCHE")
	public void testDeleteRechercheTechniqueKoAvecMessage() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = TOURISME;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final IllegalStateException panneTechnique =
				new IllegalStateException(LECTURE_TECHNIQUE_KO);

		when(gateway.findByLibelle(libelle)).thenThrow(panneTechnique);

		// ===================== ACT & ASSERT =====================
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ TypeProduitICuService.TIRET_ESPACE
								+ LECTURE_TECHNIQUE_KO);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, never()).delete(any(TypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(recherche KO technique sans message) :
	 * panne technique sans message pendant
	 * la ré-identification par libellé exact.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>n'appelle jamais {@code gateway.delete(...)}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(recherche KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testDeleteRechercheTechniqueKoSansMessage() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = OUTILLAGE;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.findByLibelle(libelle)).thenThrow(panneTechnique);

		// ===================== ACT & ASSERT =====================
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ TypeProduitICuService.TIRET_ESPACE
								+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, never()).delete(any(TypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(introuvable) : aucun objet persistant
	 * ne correspond au libellé exact.</p>
	 * <ul>
	 * <li>ne jette aucune exception</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_INTROUVABLE} + libellé</li>
	 * <li>n'appelle jamais {@code gateway.delete(...)}</li>
	 * </ul>
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
		final String libelle = VETEMENT;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);

		when(gateway.findByLibelle(libelle)).thenReturn(null);

		// ===================== ACT =====================
		service.delete(dto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelle);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, never()).delete(any(TypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(non persistant) : l'objet ré-identifié existe
	 * mais ne porte pas d'identifiant persistant.</p>
	 * <ul>
	 * <li>lève {@link ExceptionNonPersistant}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_OBJ_NON_PERSISTE} + libellé</li>
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

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = TOURISME;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final TypeProduit existant = new TypeProduit(libelle);

		existant.setIdTypeProduit(null);

		when(gateway.findByLibelle(libelle)).thenReturn(existant);

		// ===================== ACT & ASSERT =====================
		assertThatThrownBy(() -> service.delete(dto))
				.isInstanceOf(ExceptionNonPersistant.class);

		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_OBJ_NON_PERSISTE + libelle);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, never()).delete(any(TypeProduit.class));

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(KO technique de suppression avec message) :
	 * le Gateway échoue pendant la destruction.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_DELETE_KO} + libellé
	 * + tiret + message technique</li>
	 * <li>délègue bien la destruction sur l'objet persistant retrouvé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(KO technique avec message) : exception relancée + message MESSAGE_DELETE_KO + détail technique")
	public void testDeleteTechniqueKoAvecMessage() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = OUTILLAGE;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final TypeProduit tp = new TypeProduit(libelle);

		tp.setIdTypeProduit(20L);

		when(gateway.findByLibelle(libelle)).thenReturn(tp);

		final Exception ex = new Exception(MESSAGE_GATEWAY);
		doThrow(ex).when(gateway).delete(tp);

		// ===================== ACT & ASSERT =====================
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(ex);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_DELETE_KO
								+ libelle
								+ TypeProduitICuService.TIRET_ESPACE
								+ MESSAGE_GATEWAY);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).delete(tp);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(KO technique de suppression sans message) :
	 * le Gateway échoue sans message pendant la destruction.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * <li>délègue bien la destruction sur l'objet persistant retrouvé</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testDeleteTechniqueKoSansMessage() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final String libelle = BAZAR;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final TypeProduit tp = new TypeProduit(libelle);

		tp.setIdTypeProduit(21L);

		when(gateway.findByLibelle(libelle)).thenReturn(tp);

		final Exception ex = new Exception();
		doThrow(ex).when(gateway).delete(tp);

		// ===================== ACT & ASSERT =====================
		assertThatThrownBy(() -> service.delete(dto))
				.isSameAs(ex);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.MESSAGE_DELETE_KO
								+ libelle
								+ TypeProduitICuService.TIRET_ESPACE
								+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).delete(tp);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(ok) : succès nominal complet.</p>
	 * <ul>
	 * <li>délègue la destruction sur l'objet persistant retrouvé</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_DELETE_OK} + libellé</li>
	 * </ul>
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
		final String libelle = TOURISME;
		final InputDTO dto = new TypeProduitDTO.InputDTO(libelle);
		final TypeProduit tp = new TypeProduit(libelle);

		tp.setIdTypeProduit(15L);

		when(gateway.findByLibelle(libelle)).thenReturn(tp);

		// ===================== ACT =====================
		service.delete(dto);
		final String message = service.getMessage();

		// ===================== ASSERT =====================
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_DELETE_OK + libelle);

		verify(gateway, times(1)).findByLibelle(libelle);
		verify(gateway, times(1)).delete(tp);

	} // __________________________________________________________________
	

	
	// ------------------------- count() ---------------------------------//

	
	
	/**
	 * <div>
	 * <p>count(KO technique avec message) :
	 * le Gateway échoue pendant le comptage
	 * avec un message exploitable.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#KO_TECHNIQUE_RECHERCHE}
	 * + tiret + détail technique</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(KO technique avec message) : propage l'exception + message KO_TECHNIQUE_RECHERCHE + détail")
	public void testCountTechniqueKoAvecMessage() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final IllegalStateException panneTechnique =
				new IllegalStateException(LECTURE_TECHNIQUE_KO);

		when(gateway.count()).thenThrow(panneTechnique);

		// ===================== ACT & ASSERT =====================
		assertThatThrownBy(() -> service.count())
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ TypeProduitICuService.TIRET_ESPACE
								+ LECTURE_TECHNIQUE_KO);

		verify(gateway, times(1)).count();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>count(KO technique sans message) :
	 * le Gateway échoue pendant le comptage
	 * sans message exploitable.</p>
	 * <ul>
	 * <li>propage l'exception technique d'origine</li>
	 * <li>utilise le fallback
	 * {@link TypeProduitICuService#MSG_ERREUR_NON_SPECIFIEE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(KO technique sans message) : fallback MSG_ERREUR_NON_SPECIFIEE")
	public void testCountTechniqueKoSansMessage() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);
		final IllegalStateException panneTechnique = new IllegalStateException();

		when(gateway.count()).thenThrow(panneTechnique);

		// ===================== ACT & ASSERT =====================
		assertThatThrownBy(() -> service.count())
				.isSameAs(panneTechnique);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ TypeProduitICuService.TIRET_ESPACE
								+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);

		verify(gateway, times(1)).count();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>count(retour négatif) :
	 * le Gateway retourne une valeur incohérente
	 * pour un comptage observable.</p>
	 * <ul>
	 * <li>lève {@link IllegalStateException}</li>
	 * <li>positionne un message technique explicite</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(retour négatif) : IllegalStateException + message technique explicite")
	public void testCountRetourNegatifIncoherent() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		when(gateway.count()).thenReturn(-1L);

		// ===================== ACT & ASSERT =====================
		assertThatThrownBy(() -> service.count())
				.isInstanceOf(IllegalStateException.class);

		assertThat(service.getMessage())
				.isEqualTo(
						TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
								+ TypeProduitICuService.TIRET_ESPACE
								+ "comptage négatif incohérent : -1");

		verify(gateway, times(1)).count();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>count(0) : aucun résultat en stockage.</p>
	 * <ul>
	 * <li>retourne {@code 0}</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(0) : retourne 0 + message MESSAGE_RECHERCHE_VIDE")
	public void testCountZero() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		when(gateway.count()).thenReturn(0L);

		// ===================== ACT =====================
		final long retour = service.count();

		// ===================== ASSERT =====================
		assertThat(retour).isZero();
		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(gateway, times(1)).count();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>count(positif) : succès nominal du comptage.</p>
	 * <ul>
	 * <li>retourne le comptage exact</li>
	 * <li>positionne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(positif) : retourne le comptage exact + message MESSAGE_RECHERCHE_OK")
	public void testCountPositif() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		when(gateway.count()).thenReturn(42L);

		// ===================== ACT =====================
		final long retour = service.count();

		// ===================== ASSERT =====================
		assertThat(retour).isEqualTo(42L);
		assertThat(service.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(gateway, times(1)).count();

	} // __________________________________________________________________	

	
	
	// ------------------------ getMessage() ----------------------------//

	
	
	/**
	 * <div>
	 * <p>getMessage(initial) : état initial du service Mock.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>n'interagit jamais avec le Gateway</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("getMessage(initial) : retourne null + aucune interaction gateway")
	public void testGetMessageInitialNull() {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		// ======================= ACT =======================
		final String message = service.getMessage();

		// ===================== ASSERT ======================
		assertThat(message).isNull();

		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>getMessage(après erreur locale) :
	 * retourne le message courant
	 * positionné par une erreur utilisateur bénigne.</p>
	 * <ul>
	 * <li>après {@code creer(null)},
	 * retourne exactement
	 * {@link TypeProduitICuService#MESSAGE_CREER_NULL}</li>
	 * <li>n'interagit jamais avec le Gateway</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("getMessage(après erreur locale) : retourne MESSAGE_CREER_NULL")
	public void testGetMessageApresErreurLocale() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		// ======================= ACT =======================
		service.creer(null);
		final String message = service.getMessage();

		// ===================== ASSERT ======================
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_CREER_NULL);

		verifyNoInteractions(gateway);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>getMessage(après succès vide) :
	 * retourne le message courant
	 * positionné par un comptage à zéro.</p>
	 * <ul>
	 * <li>après {@code count() == 0},
	 * retourne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("getMessage(après succès vide) : retourne MESSAGE_RECHERCHE_VIDE")
	public void testGetMessageApresCountZero() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		when(gateway.count()).thenReturn(0L);

		// ======================= ACT =======================
		final long retour = service.count();
		final String message = service.getMessage();

		// ===================== ASSERT ======================
		assertThat(retour).isZero();
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(gateway, times(1)).count();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>getMessage(après succès positif) :
	 * retourne le message courant
	 * positionné par un comptage positif.</p>
	 * <ul>
	 * <li>après {@code count() > 0},
	 * retourne exactement
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("getMessage(après succès positif) : retourne MESSAGE_RECHERCHE_OK")
	public void testGetMessageApresCountPositif() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		when(gateway.count()).thenReturn(42L);

		// ======================= ACT =======================
		final long retour = service.count();
		final String message = service.getMessage();

		// ===================== ASSERT ======================
		assertThat(retour).isEqualTo(42L);
		assertThat(message)
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(gateway, times(1)).count();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>getMessage(dernier message gagne) :
	 * une opération plus récente
	 * écrase bien le message précédent.</p>
	 * <ul>
	 * <li>après une erreur locale,
	 * le message vaut d'abord
	 * {@link TypeProduitICuService#MESSAGE_CREER_NULL}</li>
	 * <li>après un {@code count()} positif,
	 * le message courant devient
	 * {@link TypeProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("getMessage(dernier message gagne) : le message le plus récent écrase le précédent")
	public void testGetMessageDernierMessageGagne() throws Exception {

		// ===================== ARRANGE =====================
		final TypeProduitGatewayIService gateway = mock(TypeProduitGatewayIService.class);
		final TypeProduitCuService service = new TypeProduitCuService(gateway);

		when(gateway.count()).thenReturn(1L);

		// ======================= ACT =======================
		service.creer(null);
		final String messageErreur = service.getMessage();

		final long retour = service.count();
		final String messageFinal = service.getMessage();

		// ===================== ASSERT ======================
		assertThat(messageErreur)
				.isEqualTo(TypeProduitICuService.MESSAGE_CREER_NULL);

		assertThat(retour).isEqualTo(1L);
		assertThat(messageFinal)
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(gateway, times(1)).count();

	} // __________________________________________________________________
	
	
	
}
