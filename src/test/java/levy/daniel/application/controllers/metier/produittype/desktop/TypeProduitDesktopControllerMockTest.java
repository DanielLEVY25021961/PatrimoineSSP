package levy.daniel.application.controllers.metier.produittype.desktop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import levy.daniel.application.controllers.metier.produittype.TypeProduitIController;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO.OutputDTO;
import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;
import levy.daniel.application.model.services.produittype.exceptionsservices.ExceptionDoublon;

/**
 * <div>
 * <p style="font-weight:bold;">CLASSE TypeProduitDesktopControllerMockTest.java :</p>
 * <p>Tests JUnit Mockito complets (avec tests "béton") du
 * CONTROLLER ADAPTER DESKTOP {@link TypeProduitDesktopController}.</p>
 * <p>Vérifie l'implémentation des contrats du PORT
 * {@link TypeProduitIController} et le dialogue avec
 * {@link TypeProduitICuService}.</p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 4 avril 2026
 */
@ExtendWith(MockitoExtension.class)
public class TypeProduitDesktopControllerMockTest {

	// *************************** CONSTANTES ******************************/

	/** Tag JUnit : tests Mockito du controller desktop. */
	public static final String TAG = "controller-desktop-mock";

	/** "outillage". */
	public static final String OUTILLAGE = "outillage";

	/** Chaine blank : "   ". */
	public static final String ESPACES = "   ";

	/** Message mock service : "message doublon service". */
	public static final String MESSAGE_SERVICE_DOUBLON
		= "message doublon service";

	// ************************* CONSTRUCTEURS *****************************/

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public TypeProduitDesktopControllerMockTest() {
		super();
	}

	// *************************** METHODES *******************************/

	
	
	// ---------------------- creer(...) --------------------------------//

	
	
	/**
	 * <div>
	 * <p>creer(null) : contrôle de surface bénin côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link TypeProduitIController#MESSAGE_CREER_VUE_NULL}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(null) : retourne null, message MESSAGE_CREER_VUE_NULL, aucune interaction service")
	public void testCreerNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.creer(null);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitIController.MESSAGE_CREER_VUE_NULL);

		verifyNoInteractions(service);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>creer(blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link TypeProduitIController#MESSAGE_CREER_VUE_BLANK}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(blank) : retourne null, message MESSAGE_CREER_VUE_BLANK, aucune interaction service")
	public void testCreerBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final InputDTO dto = new TypeProduitDTO.InputDTO(ESPACES);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.creer(dto);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitIController.MESSAGE_CREER_VUE_BLANK);

		verifyNoInteractions(service);

	} // __________________________________________________________________


	
	
	/**
	 * <div>
	 * <p>creer(ok) : scénario nominal complet.</p>
	 * <ul>
	 * <li>délègue au service</li>
	 * <li>retourne l'{@link OutputDTO} fourni</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(ok) : délégation service.creer + OutputDTO + message service mémorisé")
	public void testCreerOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final InputDTO dto = new TypeProduitDTO.InputDTO(OUTILLAGE);
		final OutputDTO cree = new TypeProduitDTO.OutputDTO(1L, OUTILLAGE, null);

		when(service.creer(dto)).thenReturn(cree);
		when(service.getMessage()).thenReturn(TypeProduitICuService.MESSAGE_CREER_OK);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.creer(dto);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdTypeProduit()).isEqualTo(1L);
		assertThat(retour.getTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_CREER_OK);

		verify(service, times(1)).creer(dto);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________


		
	/**
	 * <div>
	 * <p>creer(service KO) : propagation brute de l'exception du service.</p>
	 * <ul>
	 * <li>propage l'exception du service</li>
	 * <li>récupère quand même le message utilisateur du service</li>
	 * <li>mémorise ce message dans le controller</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(service KO) : propage l'exception + message service mémorisé")
	public void testCreerServiceKo() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final InputDTO dto = new TypeProduitDTO.InputDTO(OUTILLAGE);
		final ExceptionDoublon doublon = new ExceptionDoublon(MESSAGE_SERVICE_DOUBLON);
		final String messageService = MESSAGE_SERVICE_DOUBLON;

		when(service.creer(dto)).thenThrow(doublon);
		when(service.getMessage()).thenReturn(messageService);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.creer(dto))
				.isSameAs(doublon);

		assertThat(controller.getMessage()).isEqualTo(messageService);

		verify(service, times(1)).creer(dto);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________
	
	
	
	// ------------------- rechercherTous() ------------------------------//

	
		
	/**
	 * <div>
	 * <p>rechercherTous(ok) : scénario nominal complet.</p>
	 * <ul>
	 * <li>délègue au service</li>
	 * <li>retourne la liste fournie par le service</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTous(ok) : délégation service + liste + message service mémorisé")
	public void testRechercherTousOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final OutputDTO dto = new TypeProduitDTO.OutputDTO(1L, OUTILLAGE, null);

		when(service.rechercherTous()).thenReturn(java.util.List.of(dto));
		when(service.getMessage()).thenReturn(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.rechercherTous();
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(1);
		assertThat(retour)
				.extracting(TypeProduitDTO.OutputDTO::getIdTypeProduit)
				.containsExactly(1L);
		assertThat(retour)
				.extracting(TypeProduitDTO.OutputDTO::getTypeProduit)
				.containsExactly(OUTILLAGE);
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(service, times(1)).rechercherTous();
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>rechercherTous(service KO) : propagation brute de l'exception du service.</p>
	 * <ul>
	 * <li>propage l'exception du service</li>
	 * <li>récupère quand même le message utilisateur du service</li>
	 * <li>mémorise ce message dans le controller</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTous(service KO) : propage l'exception + message service mémorisé")
	public void testRechercherTousServiceKo() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final IllegalStateException panneTechnique
			= new IllegalStateException(TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ TypeProduitICuService.TIRET_ESPACE
				+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

		when(service.rechercherTous()).thenThrow(panneTechnique);
		when(service.getMessage()).thenReturn(messageService);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.rechercherTous())
				.isSameAs(panneTechnique);

		assertThat(controller.getMessage()).isEqualTo(messageService);

		verify(service, times(1)).rechercherTous();
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________

	
	
	// ------------------ rechercherTousString() -------------------------//

		

}