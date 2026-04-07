/* ********************************************************************* */
/* ********************* TEST MOCKITO CONTROLLER WEB ******************* */
/* ********************************************************************* */
package levy.daniel.application.controllers.metier.produittype.web;

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

import levy.daniel.application.controllers.metier.produittype.SousTypeProduitIController;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO.OutputDTO;
import levy.daniel.application.model.services.produittype.cu.SousTypeProduitICuService;

/**
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE SousTypeProduitWebControllerMockTest.java :
 * </p>
 * <p>
 * Tests JUnit Mockito complets du CONTROLLER ADAPTER WEB
 * {@link SousTypeProduitWebController}.
 * </p>
 * <p>
 * Vérifie l'implémentation des contrats du PORT
 * {@link SousTypeProduitIController}
 * et le dialogue avec
 * {@link SousTypeProduitICuService}.
 * </p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 7 avril 2026
 */
@ExtendWith(MockitoExtension.class)
public class SousTypeProduitWebControllerMockTest {

	// *************************** CONSTANTES ******************************/

	/** Tag JUnit : tests Mockito du controller web. */
	public static final String TAG = "controller-web-mock";

	/** TypeProduit parent : "bazar". */
	public static final String BAZAR = "bazar";

	/** SousTypeProduit : "outillage". */
	public static final String OUTILLAGE = "outillage";

	/** Chaine blank : "   ". */
	public static final String ESPACES = "   ";

	// ************************* CONSTRUCTEURS *****************************/

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public SousTypeProduitWebControllerMockTest() {
		super();
	}

	// *************************** METHODES *******************************/

	
	
	// ---------------------- creer(...) --------------------------------//

	
	
	/**
	 * <div>
	 * <p>creer(null) : contrôle de surface bénin côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link SousTypeProduitIController#MESSAGE_CREER_VUE_NULL}</li>
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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.creer(null);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitIController.MESSAGE_CREER_VUE_NULL);

		verifyNoInteractions(service);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>creer(blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link SousTypeProduitIController#MESSAGE_CREER_VUE_BLANK}</li>
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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, ESPACES);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.creer(dto);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitIController.MESSAGE_CREER_VUE_BLANK);

		verifyNoInteractions(service);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>creer(parent blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link SousTypeProduitIController#MESSAGE_CREER_VUE_PARENT_BLANK}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("creer(parent blank) : retourne null, message MESSAGE_CREER_VUE_PARENT_BLANK, aucune interaction service")
	public void testCreerParentBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(ESPACES, OUTILLAGE);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.creer(dto);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(SousTypeProduitIController.MESSAGE_CREER_VUE_PARENT_BLANK);

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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);
		final OutputDTO cree = new SousTypeProduitDTO.OutputDTO(1L, BAZAR, OUTILLAGE, null);

		when(service.creer(dto)).thenReturn(cree);
		when(service.getMessage()).thenReturn(SousTypeProduitICuService.MESSAGE_CREER_OK);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.creer(dto);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdSousTypeProduit()).isEqualTo(1L);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_CREER_OK);

		verify(service, times(1)).creer(dto);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>creer(parent absent service KO) : propagation brute de l'exception du service.</p>
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
	@DisplayName("creer(parent absent service KO) : propage l'exception + message service mémorisé")
	public void testCreerParentAbsentServiceKo() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);
		final InputDTO dto = new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final IllegalStateException parentAbsent
			= new IllegalStateException(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		when(service.creer(dto)).thenThrow(parentAbsent);
		when(service.getMessage()).thenReturn(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.creer(dto))
				.isSameAs(parentAbsent);

		assertThat(controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_PAS_PARENT);

		verify(service, times(1)).creer(dto);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________

	
	
	// -------------------- rechercherTous() ----------------------------//

	
	
	// ------------------ rechercherTousString() ------------------------//

	
	
	// ---------------- rechercherTousParPage(...) ----------------------//

	
	
	// -------------------- findByLibelle(...) --------------------------//

	
	
	// ----------------- findByLibelleRapide(...) -----------------------//

	
	
	// ------------------- findAllByParent(...) -------------------------//

	
	
	// ---------------------- findByDTO(...) ----------------------------//

	
	
	// ----------------------- findById(...) ----------------------------//

	
	
	// ------------------------ update(...) -----------------------------//

	
	
	// ------------------------ delete(...) -----------------------------//

	
	
	// -------------------------- count() -------------------------------//

	
	
	// ------------------------ getMessage() ----------------------------//

	
	
} // FIN DE LA CLASSE SousTypeProduitWebControllerMockTest.----------------