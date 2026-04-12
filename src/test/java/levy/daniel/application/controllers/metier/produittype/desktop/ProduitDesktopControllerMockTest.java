/* ********************************************************************* */
/* ******************* TEST MOCKITO CONTROLLER DESKTOP ***************** */
/* ********************************************************************* */
package levy.daniel.application.controllers.metier.produittype.desktop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
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

import levy.daniel.application.controllers.metier.produittype.ProduitIController;
import levy.daniel.application.model.dto.produittype.ProduitDTO;
import levy.daniel.application.model.dto.produittype.ProduitDTO.InputDTO;
import levy.daniel.application.model.dto.produittype.ProduitDTO.OutputDTO;
import levy.daniel.application.model.dto.produittype.SousTypeProduitDTO;
import levy.daniel.application.model.services.produittype.cu.ProduitICuService;

/**
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE ProduitDesktopControllerMockTest.java :
 * </p>
 * 
 * <ul>
 * <li>Tests JUnit Mockito complets (avec tests "béton") du 
 * CONTROLLER ADAPTER DESKTOP
 * {@link ProduitDesktopController}.</li>
 * <li>Vérifie l'implémentation des contrats du PORT
 * {@link ProduitIController}
 * et le dialogue avec {@link ProduitICuService}.</li>
 * <li>
 * Aucun profil Spring n'est activé dans cette classe,
 * car ce test Mockito ne démarre pas de contexte Spring :
 * le service est mocké à la main
 * et le controller est instancié directement.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 9 avril 2026
 */
@ExtendWith(MockitoExtension.class)
public class ProduitDesktopControllerMockTest {

	// *************************** CONSTANTES ******************************/

	/** Tag JUnit : tests Mockito du controller desktop. */
	public static final String TAG = "controller-desktop-mock";

	/** TypeProduit parent : "bazar". */
	public static final String BAZAR = "bazar";

	/** SousTypeProduit parent : "outillage". */
	public static final String OUTILLAGE = "outillage";

	/** Produit : "marteau". */
	public static final String MARTEAU = "marteau";

	/** Chaine blank : "   ". */
	public static final String ESPACES = "   ";

	/**
	 * "tourisme"
	 */
	public static final String TOURISME = "tourisme";

	/**
	 * "guide"
	 */
	public static final String GUIDE = "guide";

	/**
	 * "scie"
	 */
	public static final String SCIE = "scie";
	
	/**
	 * "sous-type-produit-parent-absent"
	 */
	public static final String STP_PARENT_ABSENT 
		= "sous-type-produit-parent-absent";

	// ************************* CONSTRUCTEURS *****************************/

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public ProduitDesktopControllerMockTest() {
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
	 * {@link ProduitIController#MESSAGE_CREER_VUE_NULL}</li>
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
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.creer(null);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(ProduitIController.MESSAGE_CREER_VUE_NULL);

		verifyNoInteractions(service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>creer(blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link ProduitIController#MESSAGE_CREER_VUE_BLANK}</li>
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
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final InputDTO dto = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, ESPACES);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.creer(dto);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(ProduitIController.MESSAGE_CREER_VUE_BLANK);

		verifyNoInteractions(service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>creer(parent blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link ProduitIController#MESSAGE_CREER_VUE_PARENT_BLANK}</li>
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
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final InputDTO dto = new ProduitDTO.InputDTO(BAZAR, ESPACES, MARTEAU);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.creer(dto);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(ProduitIController.MESSAGE_CREER_VUE_PARENT_BLANK);

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
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final InputDTO dto = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);
		final OutputDTO cree = new ProduitDTO.OutputDTO(1L, BAZAR, OUTILLAGE, MARTEAU);

		when(service.creer(dto)).thenReturn(cree);
		when(service.getMessage()).thenReturn(ProduitICuService.MESSAGE_CREER_OK);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.creer(dto);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdProduit()).isEqualTo(1L);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(retour.getProduit()).isEqualTo(MARTEAU);
		assertThat(message).isEqualTo(ProduitICuService.MESSAGE_CREER_OK);

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
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final InputDTO dto
			= new ProduitDTO.InputDTO(BAZAR, STP_PARENT_ABSENT, MARTEAU);

		final IllegalStateException parentAbsent
			= new IllegalStateException(ProduitICuService.MESSAGE_PAS_PARENT);

		when(service.creer(dto)).thenThrow(parentAbsent);
		when(service.getMessage()).thenReturn(ProduitICuService.MESSAGE_PAS_PARENT);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.creer(dto))
				.isSameAs(parentAbsent);

		assertThat(controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		verify(service, times(1)).creer(dto);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	// -------------------- rechercherTous() ----------------------------//



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
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);

		final OutputDTO dto
			= new ProduitDTO.OutputDTO(1L, BAZAR, OUTILLAGE, MARTEAU);

		when(service.rechercherTous()).thenReturn(java.util.List.of(dto));
		when(service.getMessage()).thenReturn(ProduitICuService.MESSAGE_RECHERCHE_OK);

		/* ======================= ACT ======================= */
		final java.util.List<ProduitDTO.OutputDTO> retour
			= controller.rechercherTous();
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(1);
		assertThat(retour)
				.extracting(ProduitDTO.OutputDTO::getIdProduit)
				.containsExactly(1L);
		assertThat(retour)
				.extracting(ProduitDTO.OutputDTO::getTypeProduit)
				.containsExactly(BAZAR);
		assertThat(retour)
				.extracting(ProduitDTO.OutputDTO::getSousTypeProduit)
				.containsExactly(OUTILLAGE);
		assertThat(retour)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.containsExactly(MARTEAU);
		assertThat(message).isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

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
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);

		final IllegalStateException panneTechnique
			= new IllegalStateException(ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= ProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ ProduitICuService.TIRET_ESPACE
				+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

		when(service.rechercherTous()).thenThrow(panneTechnique);
		when(service.getMessage()).thenReturn(messageService);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.rechercherTous())
				.isSameAs(panneTechnique);

		assertThat(controller.getMessage()).isEqualTo(messageService);

		verify(service, times(1)).rechercherTous();
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTous(vide) : scénario nominal vide.</p>
	 * <ul>
	 * <li>délègue au service</li>
	 * <li>retourne une liste vide</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTous(vide) : délégation service + liste vide + message service mémorisé")
	public void testRechercherTousVide() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);

		when(service.rechercherTous()).thenReturn(java.util.Collections.emptyList());
		when(service.getMessage()).thenReturn(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* ======================= ACT ======================= */
		final java.util.List<ProduitDTO.OutputDTO> retour
			= controller.rechercherTous();
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message).isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(service, times(1)).rechercherTous();
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	// ------------------ rechercherTousString() ------------------------//



	/**
	 * <div>
	 * <p>rechercherTousString(ok) : scénario nominal complet.</p>
	 * <ul>
	 * <li>délègue au service</li>
	 * <li>retourne la liste de String fournie par le service</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousString(ok) : délégation service + liste de String + message service mémorisé")
	public void testRechercherTousStringOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);

		when(service.rechercherTousString())
				.thenReturn(java.util.List.of(MARTEAU));
		when(service.getMessage())
				.thenReturn(ProduitICuService.MESSAGE_RECHERCHE_OK);

		/* ======================= ACT ======================= */
		final java.util.List<String> retour = controller.rechercherTousString();
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).containsExactly(MARTEAU);
		assertThat(message).isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(service, times(1)).rechercherTousString();
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousString(service KO) : propagation brute de l'exception du service.</p>
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
	@DisplayName("rechercherTousString(service KO) : propage l'exception + message service mémorisé")
	public void testRechercherTousStringServiceKo() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final IllegalStateException panneTechnique
			= new IllegalStateException(ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= ProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ ProduitICuService.TIRET_ESPACE
				+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

		when(service.rechercherTousString()).thenThrow(panneTechnique);
		when(service.getMessage()).thenReturn(messageService);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.rechercherTousString())
				.isSameAs(panneTechnique);

		assertThat(controller.getMessage()).isEqualTo(messageService);

		verify(service, times(1)).rechercherTousString();
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousString(vide) : scénario nominal vide.</p>
	 * <ul>
	 * <li>délègue au service</li>
	 * <li>retourne une liste vide</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousString(vide) : liste vide + message service mémorisé")
	public void testRechercherTousStringVide() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);

		when(service.rechercherTousString()).thenReturn(java.util.Collections.emptyList());
		when(service.getMessage()).thenReturn(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* ======================= ACT ======================= */
		final java.util.List<String> retour = controller.rechercherTousString();
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message).isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(service, times(1)).rechercherTousString();
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	// ---------------- rechercherTousParPage(...) ----------------------//



	/**
	 * <div>
	 * <p>rechercherTousParPage(null) : contrôle de surface bénin côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link ProduitIController#MESSAGE_RECHERCHE_PAGINEE_REQUETE_NULL}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousParPage(null) : retourne null + message local + aucune interaction service")
	public void testRechercherTousParPageNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);

		/* ======================= ACT ======================= */
		final levy.daniel.application.model.dto.pagination.ResultatPageDTO<OutputDTO> retour
			= controller.rechercherTousParPage(null);

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(controller.getMessage())
				.isEqualTo(
						ProduitIController
							.MESSAGE_RECHERCHE_PAGINEE_REQUETE_NULL);

		verifyNoInteractions(service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousParPage(défaut) : scénario nominal avec pagination DTO par défaut.</p>
	 * <ul>
	 * <li>délègue au service avec page interne 0-based</li>
	 * <li>retourne un
	 * {@link levy.daniel.application.model.dto.pagination.ResultatPageDTO}
	 * cohérent pour la VUE</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousParPage(défaut) : page 1 / taille 10 + conversion interne 0-based + message service mémorisé")
	public void testRechercherTousParPageDefautOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final levy.daniel.application.model.dto.pagination.RequetePageDTO requeteDTO
			= new levy.daniel.application.model.dto.pagination.RequetePageDTO();
		final levy.daniel.application.model.services.produittype.pagination.ResultatPage<OutputDTO> reponseInterne
			= new levy.daniel.application.model.services.produittype.pagination.ResultatPage<OutputDTO>(
					java.util.Collections.emptyList(),
					0,
					10,
					0L);

		when(service.rechercherTousParPage(
				org.mockito.ArgumentMatchers.any(
						levy.daniel.application.model.services.produittype.pagination.RequetePage.class)))
				.thenReturn(reponseInterne);
		when(service.getMessage())
				.thenReturn(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		/* ======================= ACT ======================= */
		final levy.daniel.application.model.dto.pagination.ResultatPageDTO<OutputDTO> retour
			= controller.rechercherTousParPage(requeteDTO);

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour.getPageNumber()).isEqualTo(1);
		assertThat(retour.getPageSize()).isEqualTo(10);
		assertThat(retour.getTotalElements()).isEqualTo(0L);
		assertThat(retour.getTotalPages()).isZero();
		assertThat(retour.getContent()).isEmpty();
		assertThat(controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		verify(service, times(1)).rechercherTousParPage(
				org.mockito.ArgumentMatchers.argThat(requete ->
						requete != null
						&& requete.getPageNumber() == 0
						&& requete.getPageSize() == 10
						&& requete.getTris() != null
						&& requete.getTris().isEmpty()));
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousParPage(ok) : scénario nominal complet avec conversion
	 * pagination + tri.</p>
	 * <ul>
	 * <li>convertit la page humaine en page interne 0-based</li>
	 * <li>convertit le tri DTO en tri interne</li>
	 * <li>reconvertit le résultat paginé interne en résultat DTO pour la VUE</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousParPage(ok) : conversion pagination/tri + ResultatPageDTO + message service mémorisé")
	public void testRechercherTousParPageOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);

		final levy.daniel.application.model.dto.pagination.TriSpecDTO triDTO
			= new levy.daniel.application.model.dto.pagination.TriSpecDTO(
					"produit",
					levy.daniel.application.model.dto.pagination.DirectionTriDTO.DESC);

		final levy.daniel.application.model.dto.pagination.RequetePageDTO requeteDTO
			= new levy.daniel.application.model.dto.pagination.RequetePageDTO(
					2,
					3,
					java.util.List.of(triDTO));

		final OutputDTO dto1 = new ProduitDTO.OutputDTO(1L, BAZAR, OUTILLAGE, MARTEAU);
		final OutputDTO dto2 = new ProduitDTO.OutputDTO(2L, TOURISME, GUIDE, SCIE);

		final levy.daniel.application.model.services.produittype.pagination.ResultatPage<OutputDTO> reponseInterne
			= new levy.daniel.application.model.services.produittype.pagination.ResultatPage<OutputDTO>(
					java.util.List.of(dto1, dto2),
					1,
					3,
					7L);

		when(service.rechercherTousParPage(
				org.mockito.ArgumentMatchers.any(
						levy.daniel.application.model.services.produittype.pagination.RequetePage.class)))
				.thenReturn(reponseInterne);
		when(service.getMessage())
				.thenReturn(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		/* ======================= ACT ======================= */
		final levy.daniel.application.model.dto.pagination.ResultatPageDTO<OutputDTO> retour
			= controller.rechercherTousParPage(requeteDTO);

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour.getPageNumber()).isEqualTo(2);
		assertThat(retour.getPageSize()).isEqualTo(3);
		assertThat(retour.getTotalElements()).isEqualTo(7L);
		assertThat(retour.getTotalPages()).isEqualTo(3);
		assertThat(retour.getContent()).hasSize(2);
		assertThat(retour.getContent())
				.extracting(ProduitDTO.OutputDTO::getIdProduit)
				.containsExactly(1L, 2L);
		assertThat(retour.getContent())
				.extracting(ProduitDTO.OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, TOURISME);
		assertThat(retour.getContent())
				.extracting(ProduitDTO.OutputDTO::getSousTypeProduit)
				.containsExactly(OUTILLAGE, GUIDE);
		assertThat(retour.getContent())
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.containsExactly(MARTEAU, SCIE);
		assertThat(controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		verify(service, times(1)).rechercherTousParPage(
				org.mockito.ArgumentMatchers.argThat(requete ->
						requete != null
						&& requete.getPageNumber() == 1
						&& requete.getPageSize() == 3
						&& requete.getTris() != null
						&& requete.getTris().size() == 1
						&& "produit".equals(requete.getTris().get(0).getPropriete())
						&& requete.getTris().get(0).getDirection()
							== levy.daniel.application.model.services.produittype.pagination.DirectionTri.DESC));
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>rechercherTousParPage(service KO) : propagation brute de l'exception du service.</p>
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
	@DisplayName("rechercherTousParPage(service KO) : propage l'exception + message service mémorisé")
	public void testRechercherTousParPageServiceKo() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final levy.daniel.application.model.dto.pagination.RequetePageDTO requeteDTO
			= new levy.daniel.application.model.dto.pagination.RequetePageDTO(1, 10);

		final IllegalStateException panneTechnique
			= new IllegalStateException(ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= ProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ ProduitICuService.TIRET_ESPACE
				+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

		when(service.rechercherTousParPage(
				org.mockito.ArgumentMatchers.any(
						levy.daniel.application.model.services.produittype.pagination.RequetePage.class)))
				.thenThrow(panneTechnique);
		when(service.getMessage()).thenReturn(messageService);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.rechercherTousParPage(requeteDTO))
				.isSameAs(panneTechnique);

		assertThat(controller.getMessage()).isEqualTo(messageService);

		verify(service, times(1)).rechercherTousParPage(
				org.mockito.ArgumentMatchers.argThat(requete ->
						requete != null
						&& requete.getPageNumber() == 0
						&& requete.getPageSize() == 10));
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	// -------------------- findByLibelle(...) --------------------------//



	/**
	 * <div>
	 * <p>findByLibelle(null) : contrôle de surface bénin côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link ProduitIController#MESSAGE_FIND_BY_LIBELLE_VUE_NULL}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(null) : retourne null + message local + aucune interaction service")
	public void testFindByLibelleNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findByLibelle(null);

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(controller.getMessage())
				.isEqualTo(ProduitIController.MESSAGE_FIND_BY_LIBELLE_VUE_NULL);

		verifyNoInteractions(service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link ProduitIController#MESSAGE_FIND_BY_LIBELLE_VUE_BLANK}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(blank) : retourne null + message local + aucune interaction service")
	public void testFindByLibelleBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findByLibelle(ESPACES);

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(controller.getMessage())
				.isEqualTo(ProduitIController.MESSAGE_FIND_BY_LIBELLE_VUE_BLANK);

		verifyNoInteractions(service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(ok) : scénario nominal complet.</p>
	 * <ul>
	 * <li>délègue au service</li>
	 * <li>retourne la liste fournie</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(ok) : délégation service + liste + message service mémorisé")
	public void testFindByLibelleOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final String libelle = MARTEAU;

		final OutputDTO trouveA
			= new ProduitDTO.OutputDTO(1L, BAZAR, OUTILLAGE, libelle);
		final OutputDTO trouveB
			= new ProduitDTO.OutputDTO(2L, TOURISME, GUIDE, libelle);

		when(service.findByLibelle(libelle))
				.thenReturn(java.util.List.of(trouveA, trouveB));
		when(service.getMessage())
				.thenReturn(ProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findByLibelle(libelle);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);
		assertThat(retour)
				.extracting(ProduitDTO.OutputDTO::getIdProduit)
				.containsExactly(1L, 2L);
		assertThat(retour)
				.extracting(ProduitDTO.OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, TOURISME);
		assertThat(retour)
				.extracting(ProduitDTO.OutputDTO::getSousTypeProduit)
				.containsExactly(OUTILLAGE, GUIDE);
		assertThat(retour)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.containsExactly(libelle, libelle);
		assertThat(message).isEqualTo(ProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		verify(service, times(1)).findByLibelle(libelle);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(absent) : scénario nominal sans résultat.</p>
	 * <ul>
	 * <li>délègue au service</li>
	 * <li>retourne une liste vide</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(absent) : liste vide + message service mémorisé")
	public void testFindByLibelleAbsent() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final String libelle = "produit-introuvable";
		final String messageService
			= ProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelle;

		when(service.findByLibelle(libelle)).thenReturn(java.util.Collections.emptyList());
		when(service.getMessage()).thenReturn(messageService);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findByLibelle(libelle);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message).isEqualTo(messageService);

		verify(service, times(1)).findByLibelle(libelle);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(service KO) : propagation brute de l'exception du service.</p>
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
	@DisplayName("findByLibelle(service KO) : propage l'exception + message service mémorisé")
	public void testFindByLibelleServiceKo() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final String libelle = MARTEAU;

		final IllegalStateException panneTechnique
			= new IllegalStateException(ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= ProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ ProduitICuService.TIRET_ESPACE
				+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

		when(service.findByLibelle(libelle)).thenThrow(panneTechnique);
		when(service.getMessage()).thenReturn(messageService);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.findByLibelle(libelle))
				.isSameAs(panneTechnique);

		assertThat(controller.getMessage()).isEqualTo(messageService);

		verify(service, times(1)).findByLibelle(libelle);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	// ----------------- findByLibelleRapide(...) -----------------------//



	/**
	 * <div>
	 * <p>findByLibelleRapide(null) : contrôle de surface bénin côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link ProduitIController#MESSAGE_FIND_BY_LIBELLE_RAPIDE_VUE_NULL}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(null) : retourne null + message local + aucune interaction service")
	public void testFindByLibelleRapideNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findByLibelleRapide(null);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(
						ProduitIController
							.MESSAGE_FIND_BY_LIBELLE_RAPIDE_VUE_NULL);

		verifyNoInteractions(service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelleRapide(blank) : le controller délègue au service.</p>
	 * <ul>
	 * <li>ne bloque pas localement un contenu blank</li>
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
	@DisplayName("findByLibelleRapide(blank) : délégation service + liste + message service mémorisé")
	public void testFindByLibelleRapideBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final OutputDTO dto = new ProduitDTO.OutputDTO(1L, BAZAR, OUTILLAGE, MARTEAU);
		final java.util.List<OutputDTO> trouves = java.util.List.of(dto);

		when(service.findByLibelleRapide(ESPACES)).thenReturn(trouves);
		when(service.getMessage()).thenReturn(ProduitICuService.MESSAGE_RECHERCHE_OK);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findByLibelleRapide(ESPACES);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(1);
		assertThat(retour)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.containsExactly(MARTEAU);
		assertThat(message).isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(service, times(1)).findByLibelleRapide(ESPACES);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelleRapide(ok) : scénario nominal complet.</p>
	 * <ul>
	 * <li>délègue au service</li>
	 * <li>retourne la liste fournie</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(ok) : délégation service + liste + message service mémorisé")
	public void testFindByLibelleRapideOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final String contenu = "mar";

		final OutputDTO dto1 = new ProduitDTO.OutputDTO(1L, BAZAR, OUTILLAGE, MARTEAU);
		final OutputDTO dto2 = new ProduitDTO.OutputDTO(2L, TOURISME, GUIDE, MARTEAU);
		final java.util.List<OutputDTO> trouves = java.util.List.of(dto1, dto2);

		when(service.findByLibelleRapide(contenu)).thenReturn(trouves);
		when(service.getMessage()).thenReturn(ProduitICuService.MESSAGE_RECHERCHE_OK);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findByLibelleRapide(contenu);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);
		assertThat(retour)
				.extracting(ProduitDTO.OutputDTO::getIdProduit)
				.containsExactly(1L, 2L);
		assertThat(retour)
				.extracting(ProduitDTO.OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, TOURISME);
		assertThat(retour)
				.extracting(ProduitDTO.OutputDTO::getSousTypeProduit)
				.containsExactly(OUTILLAGE, GUIDE);
		assertThat(retour)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.containsExactly(MARTEAU, MARTEAU);
		assertThat(message).isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(service, times(1)).findByLibelleRapide(contenu);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelleRapide(absent) : scénario nominal sans résultat.</p>
	 * <ul>
	 * <li>délègue au service</li>
	 * <li>retourne une liste vide</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelleRapide(absent) : liste vide + message service mémorisé")
	public void testFindByLibelleRapideAbsent() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final String contenu = "produit-introuvable";

		when(service.findByLibelleRapide(contenu)).thenReturn(java.util.Collections.emptyList());
		when(service.getMessage()).thenReturn(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findByLibelleRapide(contenu);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message).isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(service, times(1)).findByLibelleRapide(contenu);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelleRapide(service KO) : propagation brute de l'exception du service.</p>
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
	@DisplayName("findByLibelleRapide(service KO) : propage l'exception + message service mémorisé")
	public void testFindByLibelleRapideServiceKo() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final String contenu = MARTEAU;

		final IllegalStateException panneTechnique
			= new IllegalStateException(ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= ProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ ProduitICuService.TIRET_ESPACE
				+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

		when(service.findByLibelleRapide(contenu)).thenThrow(panneTechnique);
		when(service.getMessage()).thenReturn(messageService);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.findByLibelleRapide(contenu))
				.isSameAs(panneTechnique);

		assertThat(controller.getMessage()).isEqualTo(messageService);

		verify(service, times(1)).findByLibelleRapide(contenu);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	// ------------------- findAllByParent(...) -------------------------//



	/**
	 * <div>
	 * <p>findAllByParent(null) : contrôle de surface bénin côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link ProduitIController#MESSAGE_FIND_ALL_BY_PARENT_VUE_NULL}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(null) : retourne null + message local + aucune interaction service")
	public void testFindAllByParentNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findAllByParent(null);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(ProduitIController.MESSAGE_FIND_ALL_BY_PARENT_VUE_NULL);

		verifyNoInteractions(service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(parent blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link ProduitIController#MESSAGE_FIND_ALL_BY_PARENT_VUE_PARENT_BLANK}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(parent blank) : retourne null + message local + aucune interaction service")
	public void testFindAllByParentParentBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final SousTypeProduitDTO.InputDTO parentDto
			= new SousTypeProduitDTO.InputDTO(BAZAR, ESPACES);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findAllByParent(parentDto);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(
						ProduitIController.MESSAGE_FIND_ALL_BY_PARENT_VUE_PARENT_BLANK);

		verifyNoInteractions(service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(vide) : le controller délègue au service.</p>
	 * <ul>
	 * <li>retourne une liste vide</li>
	 * <li>positionne exactement
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>mémorise le message du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(vide) : liste vide + message service mémorisé")
	public void testFindAllByParentVide() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final SousTypeProduitDTO.InputDTO parentDto
			= new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		when(service.findAllByParent(parentDto))
				.thenReturn(java.util.Collections.emptyList());
		when(service.getMessage())
				.thenReturn(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findAllByParent(parentDto);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message)
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(service, times(1)).findAllByParent(parentDto);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(ok) : scénario nominal complet.</p>
	 * <ul>
	 * <li>délègue au service</li>
	 * <li>retourne la liste fournie</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findAllByParent(ok) : délégation service + liste + message service mémorisé")
	public void testFindAllByParentOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final SousTypeProduitDTO.InputDTO parentDto
			= new SousTypeProduitDTO.InputDTO(BAZAR, OUTILLAGE);

		final OutputDTO dto1 = new ProduitDTO.OutputDTO(1L, BAZAR, OUTILLAGE, MARTEAU);
		final OutputDTO dto2 = new ProduitDTO.OutputDTO(2L, BAZAR, OUTILLAGE, SCIE);
		final java.util.List<OutputDTO> trouves = java.util.List.of(dto1, dto2);

		when(service.findAllByParent(parentDto)).thenReturn(trouves);
		when(service.getMessage()).thenReturn(ProduitICuService.MESSAGE_RECHERCHE_OK);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findAllByParent(parentDto);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);

		assertThat(retour)
				.extracting(ProduitDTO.OutputDTO::getIdProduit)
				.containsExactly(1L, 2L);

		assertThat(retour)
				.extracting(ProduitDTO.OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, BAZAR);

		assertThat(retour)
				.extracting(ProduitDTO.OutputDTO::getSousTypeProduit)
				.containsExactly(OUTILLAGE, OUTILLAGE);

		assertThat(retour)
				.extracting(ProduitDTO.OutputDTO::getProduit)
				.containsExactly(MARTEAU, SCIE);

		assertThat(message)
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);

		verify(service, times(1)).findAllByParent(parentDto);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findAllByParent(parent absent service KO) : propagation brute de l'exception du service.</p>
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
	@DisplayName("findAllByParent(parent absent service KO) : propage l'exception + message service mémorisé")
	public void testFindAllByParentParentAbsentServiceKo() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final SousTypeProduitDTO.InputDTO parentDto
			= new SousTypeProduitDTO.InputDTO(BAZAR, STP_PARENT_ABSENT);
		final IllegalStateException panneTechnique
			= new IllegalStateException(ProduitICuService.MESSAGE_PAS_PARENT);

		when(service.findAllByParent(parentDto)).thenThrow(panneTechnique);
		when(service.getMessage()).thenReturn(ProduitICuService.MESSAGE_PAS_PARENT);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.findAllByParent(parentDto))
				.isSameAs(panneTechnique);

		assertThat(controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		verify(service, times(1)).findAllByParent(parentDto);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	// ---------------------- findByDTO(...) ----------------------------//



	/**
	 * <div>
	 * <p>findByDTO(null) : contrôle de surface bénin côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitIController#MESSAGE_FIND_BY_DTO_VUE_NULL}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(null) : retourne null + message local + aucune interaction service")
	public void testFindByDTONull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.findByDTO(null);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(ProduitIController.MESSAGE_FIND_BY_DTO_VUE_NULL);

		verifyNoInteractions(service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(parent blank) : le controller délègue au service.</p>
	 * <ul>
	 * <li>ne bloque pas localement un InputDTO portant un parent blank</li>
	 * <li>laisse le service lever l'exception</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(parent blank) : propage l'exception + message service mémorisé")
	public void testFindByDTOParentBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final InputDTO inputDTO = new ProduitDTO.InputDTO(BAZAR, ESPACES, MARTEAU);
		final IllegalStateException panneTechnique
			= new IllegalStateException(ProduitICuService.MESSAGE_PAS_PARENT);

		when(service.findByDTO(inputDTO)).thenThrow(panneTechnique);
		when(service.getMessage()).thenReturn(ProduitICuService.MESSAGE_PAS_PARENT);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.findByDTO(inputDTO))
				.isSameAs(panneTechnique);

		assertThat(controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		verify(service, times(1)).findByDTO(inputDTO);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(absent) : scénario nominal sans résultat.</p>
	 * <ul>
	 * <li>délègue au service</li>
	 * <li>retourne {@code null}</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByDTO(absent) : retourne null + message service mémorisé")
	public void testFindByDTOAbsent() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final InputDTO inputDTO
			= new ProduitDTO.InputDTO(BAZAR, "sous-type-produit-absent", MARTEAU);

		when(service.findByDTO(inputDTO)).thenReturn(null);
		when(service.getMessage()).thenReturn(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.findByDTO(inputDTO);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(service, times(1)).findByDTO(inputDTO);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(ok) : scénario nominal complet.</p>
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
	@DisplayName("findByDTO(ok) : délégation service + OutputDTO + message service mémorisé")
	public void testFindByDTOOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final InputDTO inputDTO = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);
		final OutputDTO trouve = new ProduitDTO.OutputDTO(1L, BAZAR, OUTILLAGE, MARTEAU);

		when(service.findByDTO(inputDTO)).thenReturn(trouve);
		when(service.getMessage()).thenReturn(ProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.findByDTO(inputDTO);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdProduit()).isEqualTo(1L);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(retour.getProduit()).isEqualTo(MARTEAU);
		assertThat(message)
				.isEqualTo(ProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		verify(service, times(1)).findByDTO(inputDTO);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(service KO) : propagation brute de l'exception du service.</p>
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
	@DisplayName("findByDTO(service KO) : propage l'exception + message service mémorisé")
	public void testFindByDTOServiceKo() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final InputDTO inputDTO = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);

		final IllegalStateException panneTechnique
			= new IllegalStateException(ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= ProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ ProduitICuService.TIRET_ESPACE
				+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

		when(service.findByDTO(inputDTO)).thenThrow(panneTechnique);
		when(service.getMessage()).thenReturn(messageService);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.findByDTO(inputDTO))
				.isSameAs(panneTechnique);

		assertThat(controller.getMessage()).isEqualTo(messageService);

		verify(service, times(1)).findByDTO(inputDTO);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	// ----------------------- findById(...) ----------------------------//



	/**
	 * <div>
	 * <p>findById(null) : contrôle de surface bénin côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitIController#MESSAGE_FIND_BY_ID_VUE_NULL}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(null) : retourne null + message local + aucune interaction service")
	public void testFindByIdNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.findById(null);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(ProduitIController.MESSAGE_FIND_BY_ID_VUE_NULL);

		verifyNoInteractions(service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findById(ok) : scénario nominal complet.</p>
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
	@DisplayName("findById(ok) : délégation service + OutputDTO + message service mémorisé")
	public void testFindByIdOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final Long id = 1L;
		final OutputDTO trouve = new ProduitDTO.OutputDTO(id, BAZAR, OUTILLAGE, MARTEAU);

		when(service.findById(id)).thenReturn(trouve);
		when(service.getMessage()).thenReturn(ProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.findById(id);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdProduit()).isEqualTo(id);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(retour.getProduit()).isEqualTo(MARTEAU);
		assertThat(message)
				.isEqualTo(ProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		verify(service, times(1)).findById(id);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findById(absent) : scénario nominal sans résultat.</p>
	 * <ul>
	 * <li>délègue au service</li>
	 * <li>retourne {@code null}</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findById(absent) : retourne null + message service mémorisé")
	public void testFindByIdAbsent() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final Long id = 999_999_999L;
		final String messageService
			= ProduitICuService.MESSAGE_OBJ_INTROUVABLE + id;

		when(service.findById(id)).thenReturn(null);
		when(service.getMessage()).thenReturn(messageService);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.findById(id);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message).isEqualTo(messageService);

		verify(service, times(1)).findById(id);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findById(service KO) : propagation brute de l'exception du service.</p>
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
	@DisplayName("findById(service KO) : propage l'exception + message service mémorisé")
	public void testFindByIdServiceKo() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final Long id = 1L;
		final IllegalStateException panneTechnique
			= new IllegalStateException(ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= ProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ ProduitICuService.TIRET_ESPACE
				+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

		when(service.findById(id)).thenThrow(panneTechnique);
		when(service.getMessage()).thenReturn(messageService);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.findById(id))
				.isSameAs(panneTechnique);

		assertThat(controller.getMessage()).isEqualTo(messageService);

		verify(service, times(1)).findById(id);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	// ------------------------ update(...) -----------------------------//



	/**
	 * <div>
	 * <p>update(null) : contrôle de surface bénin côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitIController#MESSAGE_UPDATE_VUE_NULL}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(null) : retourne null + message local + aucune interaction service")
	public void testUpdateNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.update(null);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(ProduitIController.MESSAGE_UPDATE_VUE_NULL);

		verifyNoInteractions(service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link ProduitIController#MESSAGE_UPDATE_VUE_BLANK}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(blank) : retourne null + message local + aucune interaction service")
	public void testUpdateBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final InputDTO inputDTO = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, ESPACES);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.update(inputDTO);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(ProduitIController.MESSAGE_UPDATE_VUE_BLANK);

		verifyNoInteractions(service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(parent blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link ProduitIController#MESSAGE_UPDATE_VUE_PARENT_BLANK}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(parent blank) : retourne null + message local + aucune interaction service")
	public void testUpdateParentBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final InputDTO inputDTO = new ProduitDTO.InputDTO(BAZAR, ESPACES, MARTEAU);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.update(inputDTO);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(ProduitIController.MESSAGE_UPDATE_VUE_PARENT_BLANK);

		verifyNoInteractions(service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(ok) : scénario nominal complet.</p>
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
	@DisplayName("update(ok) : délégation service + OutputDTO + message service mémorisé")
	public void testUpdateOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final InputDTO inputDTO = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);
		final OutputDTO modifie = new ProduitDTO.OutputDTO(1L, BAZAR, OUTILLAGE, MARTEAU);
		final String messageService
			= ProduitICuService.MESSAGE_MODIF_OK + MARTEAU;

		when(service.update(inputDTO)).thenReturn(modifie);
		when(service.getMessage()).thenReturn(messageService);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.update(inputDTO);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdProduit()).isEqualTo(1L);
		assertThat(retour.getTypeProduit()).isEqualTo(BAZAR);
		assertThat(retour.getSousTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(retour.getProduit()).isEqualTo(MARTEAU);
		assertThat(message).isEqualTo(messageService);

		verify(service, times(1)).update(inputDTO);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(absent) : scénario nominal sans résultat.</p>
	 * <ul>
	 * <li>délègue au service</li>
	 * <li>retourne {@code null}</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("update(absent) : retourne null + message service mémorisé")
	public void testUpdateAbsent() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final String produit = "produit-update-introuvable";
		final InputDTO inputDTO
			= new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, produit);
		final String messageService
			= ProduitICuService.MESSAGE_OBJ_INTROUVABLE + produit;

		when(service.update(inputDTO)).thenReturn(null);
		when(service.getMessage()).thenReturn(messageService);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.update(inputDTO);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message).isEqualTo(messageService);

		verify(service, times(1)).update(inputDTO);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(parent absent service KO) : propagation brute de l'exception du service.</p>
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
	@DisplayName("update(parent absent service KO) : propage l'exception + message service mémorisé")
	public void testUpdateParentAbsentServiceKo() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final InputDTO inputDTO
			= new ProduitDTO.InputDTO(BAZAR, STP_PARENT_ABSENT, MARTEAU);
		final IllegalStateException exception
			= new IllegalStateException(ProduitICuService.MESSAGE_PAS_PARENT);

		when(service.update(inputDTO)).thenThrow(exception);
		when(service.getMessage()).thenReturn(ProduitICuService.MESSAGE_PAS_PARENT);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.update(inputDTO))
				.isSameAs(exception);

		assertThat(controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		verify(service, times(1)).update(inputDTO);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>update(service KO) : propagation brute de l'exception du service.</p>
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
	@DisplayName("update(service KO) : propage l'exception + message service mémorisé")
	public void testUpdateServiceKo() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final InputDTO inputDTO = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException exception
			= new IllegalStateException(ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= ProduitICuService.MESSAGE_MODIF_KO
				+ MARTEAU
				+ ProduitICuService.TIRET_ESPACE
				+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

		when(service.update(inputDTO)).thenThrow(exception);
		when(service.getMessage()).thenReturn(messageService);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.update(inputDTO))
				.isSameAs(exception);

		assertThat(controller.getMessage()).isEqualTo(messageService);

		verify(service, times(1)).update(inputDTO);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	// ------------------------ delete(...) -----------------------------//



	/**
	 * <div>
	 * <p>delete(null) : contrôle de surface bénin côté controller.</p>
	 * <ul>
	 * <li>ne lève aucune exception</li>
	 * <li>positionne {@link ProduitIController#MESSAGE_DELETE_VUE_NULL}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(null) : message local + aucune interaction service")
	public void testDeleteNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);

		/* ======================= ACT ======================= */
		controller.delete(null);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(message)
				.isEqualTo(ProduitIController.MESSAGE_DELETE_VUE_NULL);

		verifyNoInteractions(service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>ne lève aucune exception</li>
	 * <li>positionne {@link ProduitIController#MESSAGE_DELETE_VUE_BLANK}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(blank) : message local + aucune interaction service")
	public void testDeleteBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final InputDTO inputDTO = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, ESPACES);

		/* ======================= ACT ======================= */
		controller.delete(inputDTO);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(message)
				.isEqualTo(ProduitIController.MESSAGE_DELETE_VUE_BLANK);

		verifyNoInteractions(service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(parent blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>ne lève aucune exception</li>
	 * <li>positionne
	 * {@link ProduitIController#MESSAGE_DELETE_VUE_PARENT_BLANK}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(parent blank) : message local + aucune interaction service")
	public void testDeleteParentBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final InputDTO inputDTO = new ProduitDTO.InputDTO(BAZAR, ESPACES, MARTEAU);

		/* ======================= ACT ======================= */
		controller.delete(inputDTO);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(message)
				.isEqualTo(ProduitIController.MESSAGE_DELETE_VUE_PARENT_BLANK);

		verifyNoInteractions(service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(ok) : scénario nominal complet.</p>
	 * <ul>
	 * <li>délègue au service</li>
	 * <li>ne lève aucune exception</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(ok) : délégation service + message service mémorisé")
	public void testDeleteOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final InputDTO inputDTO = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);
		final String messageService
			= ProduitICuService.MESSAGE_DELETE_OK + MARTEAU;

		when(service.getMessage()).thenReturn(messageService);

		/* ======================= ACT ======================= */
		controller.delete(inputDTO);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(message).isEqualTo(messageService);

		verify(service, times(1)).delete(inputDTO);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(absent) : scénario nominal sans suppression effective.</p>
	 * <ul>
	 * <li>délègue au service</li>
	 * <li>ne lève aucune exception</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("delete(absent) : message service mémorisé")
	public void testDeleteAbsent() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final String produit = "produit-delete-introuvable";
		final InputDTO inputDTO = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, produit);
		final String messageService
			= ProduitICuService.MESSAGE_OBJ_INTROUVABLE + produit;

		when(service.getMessage()).thenReturn(messageService);

		/* ======================= ACT ======================= */
		controller.delete(inputDTO);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(message).isEqualTo(messageService);

		verify(service, times(1)).delete(inputDTO);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(parent absent service KO) : propagation brute de l'exception du service.</p>
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
	@DisplayName("delete(parent absent service KO) : propage l'exception + message service mémorisé")
	public void testDeleteParentAbsentServiceKo() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final InputDTO inputDTO
			= new ProduitDTO.InputDTO(BAZAR, STP_PARENT_ABSENT, MARTEAU);
		final IllegalStateException exception
			= new IllegalStateException(ProduitICuService.MESSAGE_PAS_PARENT);

		doThrow(exception).when(service).delete(inputDTO);
		when(service.getMessage()).thenReturn(ProduitICuService.MESSAGE_PAS_PARENT);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.delete(inputDTO))
				.isSameAs(exception);

		assertThat(controller.getMessage())
				.isEqualTo(ProduitICuService.MESSAGE_PAS_PARENT);

		verify(service, times(1)).delete(inputDTO);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>delete(service KO) : propagation brute de l'exception du service.</p>
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
	@DisplayName("delete(service KO) : propage l'exception + message service mémorisé")
	public void testDeleteServiceKo() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final InputDTO inputDTO = new ProduitDTO.InputDTO(BAZAR, OUTILLAGE, MARTEAU);
		final IllegalStateException exception
			= new IllegalStateException(ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= ProduitICuService.MESSAGE_DELETE_KO
				+ MARTEAU
				+ ProduitICuService.TIRET_ESPACE
				+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

		doThrow(exception).when(service).delete(inputDTO);
		when(service.getMessage()).thenReturn(messageService);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.delete(inputDTO))
				.isSameAs(exception);

		assertThat(controller.getMessage()).isEqualTo(messageService);

		verify(service, times(1)).delete(inputDTO);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	// -------------------------- count() -------------------------------//



	/**
	 * <div>
	 * <p>count(vide) : scénario nominal sans résultat.</p>
	 * <ul>
	 * <li>délègue le comptage au service</li>
	 * <li>retourne {@code 0}</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(vide) : retourne 0 + message service mémorisé")
	public void testCountVide() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final long comptage = 0L;
		final String messageService = ProduitICuService.MESSAGE_RECHERCHE_VIDE;

		when(service.count()).thenReturn(comptage);
		when(service.getMessage()).thenReturn(messageService);

		/* ======================= ACT ======================= */
		final long retour = controller.count();
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isZero();
		assertThat(message).isEqualTo(messageService);

		verify(service, times(1)).count();
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>count(ok) : scénario nominal complet.</p>
	 * <ul>
	 * <li>délègue le comptage au service</li>
	 * <li>retourne le comptage fourni par le service</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("count(ok) : retourne le comptage + message service mémorisé")
	public void testCountOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final long comptage = 5L;
		final String messageService = ProduitICuService.MESSAGE_RECHERCHE_OK;

		when(service.count()).thenReturn(comptage);
		when(service.getMessage()).thenReturn(messageService);

		/* ======================= ACT ======================= */
		final long retour = controller.count();
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isEqualTo(comptage);
		assertThat(message).isEqualTo(messageService);

		verify(service, times(1)).count();
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>count(service KO) : propagation brute de l'exception du service.</p>
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
	@DisplayName("count(service KO) : propage l'exception + message service mémorisé")
	public void testCountServiceKo() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller
			= new ProduitDesktopController(service);
		final IllegalStateException panneTechnique
			= new IllegalStateException(ProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= ProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ ProduitICuService.TIRET_ESPACE
				+ ProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

		when(service.count()).thenThrow(panneTechnique);
		when(service.getMessage()).thenReturn(messageService);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.count())
				.isSameAs(panneTechnique);

		assertThat(controller.getMessage()).isEqualTo(messageService);

		verify(service, times(1)).count();
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	// ------------------------ getMessage() ----------------------------//



	/**
	 * <div>
	 * <p>getMessage(initial) : état initial du controller Mock.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@Tag(TAG)
	@DisplayName("getMessage(initial) : retourne null + aucune interaction service")
	public void testGetMessageInitialNull() {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller = new ProduitDesktopController(service);

		/* ======================= ACT ======================= */
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(message).isNull();
		verifyNoInteractions(service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>getMessage(après erreur locale) : message produit par le controller.</p>
	 * <ul>
	 * <li>après {@code creer(null)}, retourne exactement
	 * {@link ProduitIController#MESSAGE_CREER_VUE_NULL}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("getMessage(après erreur locale) : retourne MESSAGE_CREER_VUE_NULL")
	public void testGetMessageApresErreurLocale() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller = new ProduitDesktopController(service);

		/* ======================= ACT ======================= */
		controller.creer(null);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(message)
				.isEqualTo(ProduitIController.MESSAGE_CREER_VUE_NULL);
		verifyNoInteractions(service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>getMessage(après succès vide) : message relayé depuis le service.</p>
	 * <ul>
	 * <li>après {@code count() == 0}, retourne exactement
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_VIDE}</li>
	 * <li>délègue bien le comptage au service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("getMessage(après succès vide) : retourne MESSAGE_RECHERCHE_VIDE")
	public void testGetMessageApresCountZero() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller = new ProduitDesktopController(service);
		when(service.count()).thenReturn(0L);
		when(service.getMessage())
				.thenReturn(ProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* ======================= ACT ======================= */
		final long retour = controller.count();
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isZero();
		assertThat(message)
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_VIDE);
		verify(service, times(1)).count();
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>getMessage(après succès positif) : message relayé depuis le service.</p>
	 * <ul>
	 * <li>après {@code count() > 0}, retourne exactement
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * <li>délègue bien le comptage au service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("getMessage(après succès positif) : retourne MESSAGE_RECHERCHE_OK")
	public void testGetMessageApresCountPositif() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller = new ProduitDesktopController(service);

		when(service.count()).thenReturn(1L);
		when(service.getMessage())
				.thenReturn(ProduitICuService.MESSAGE_RECHERCHE_OK);

		/* ======================= ACT ======================= */
		final long retour = controller.count();
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isEqualTo(1L);
		assertThat(message)
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);
		verify(service, times(1)).count();
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>getMessage(dernier message gagne) : le message courant est écrasé.</p>
	 * <ul>
	 * <li>après une erreur locale, le message vaut d'abord
	 * {@link ProduitIController#MESSAGE_CREER_VUE_NULL}</li>
	 * <li>après un {@code count()} positif, le message courant devient
	 * {@link ProduitICuService#MESSAGE_RECHERCHE_OK}</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("getMessage(dernier message gagne) : le message le plus récent écrase le précédent")
	public void testGetMessageDernierMessageGagne() throws Exception {

		/* ===================== ARRANGE ===================== */
		final ProduitICuService service = mock(ProduitICuService.class);
		final ProduitDesktopController controller = new ProduitDesktopController(service);

		when(service.count()).thenReturn(1L);
		when(service.getMessage())
				.thenReturn(ProduitICuService.MESSAGE_RECHERCHE_OK);

		/* ======================= ACT ======================= */
		controller.creer(null);
		final String messageErreur = controller.getMessage();
		final long retour = controller.count();
		final String messageFinal = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(messageErreur)
				.isEqualTo(ProduitIController.MESSAGE_CREER_VUE_NULL);
		assertThat(retour).isEqualTo(1L);
		assertThat(messageFinal)
				.isEqualTo(ProduitICuService.MESSAGE_RECHERCHE_OK);
		verify(service, times(1)).count();
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________



} // FIN DE LA CLASSE ProduitDesktopControllerMockTest.--------------------