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
	
	/**
	 * "tourisme"
	 */
	public static final String TOURISME = "tourisme";

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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);

		final OutputDTO dto
			= new SousTypeProduitDTO.OutputDTO(1L, BAZAR, OUTILLAGE, null);

		when(service.rechercherTous()).thenReturn(java.util.List.of(dto));
		when(service.getMessage()).thenReturn(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* ======================= ACT ======================= */
		final java.util.List<SousTypeProduitDTO.OutputDTO> retour
			= controller.rechercherTous();
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(1);
		assertThat(retour)
				.extracting(SousTypeProduitDTO.OutputDTO::getIdSousTypeProduit)
				.containsExactly(1L);
		assertThat(retour)
				.extracting(SousTypeProduitDTO.OutputDTO::getTypeProduit)
				.containsExactly(BAZAR);
		assertThat(retour)
				.extracting(SousTypeProduitDTO.OutputDTO::getSousTypeProduit)
				.containsExactly(OUTILLAGE);
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);

		final IllegalStateException panneTechnique
			= new IllegalStateException(SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ SousTypeProduitICuService.TIRET_ESPACE
				+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);

		when(service.rechercherTous()).thenReturn(java.util.Collections.emptyList());
		when(service.getMessage()).thenReturn(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* ======================= ACT ======================= */
		final java.util.List<SousTypeProduitDTO.OutputDTO> retour
			= controller.rechercherTous();
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(service, times(1)).rechercherTous();
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________
	

	
	// ------------------ rechercherTousString() ------------------------//

	
	
	/**
	 * <div>
	 * <p>rechercherTousString(ok) : scénario nominal complet.</p>
	 * <ul>
	 * <li>délègue au service</li>
	 * <li>retourne la liste de libellés fournie par le service</li>
	 * <li>mémorise le message utilisateur du service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("rechercherTousString(ok) : délégation service + liste de libellés + message service mémorisé")
	public void testRechercherTousStringOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);

		when(service.rechercherTousString()).thenReturn(java.util.List.of(OUTILLAGE));
		when(service.getMessage()).thenReturn(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* ======================= ACT ======================= */
		final java.util.List<String> retour = controller.rechercherTousString();
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(1);
		assertThat(retour).containsExactly(OUTILLAGE);
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);

		final IllegalStateException panneTechnique
			= new IllegalStateException(SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ SousTypeProduitICuService.TIRET_ESPACE
				+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

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
	@DisplayName("rechercherTousString(vide) : délégation service + liste vide + message service mémorisé")
	public void testRechercherTousStringVide() throws Exception {

		/* ===================== ARRANGE ===================== */
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);

		when(service.rechercherTousString()).thenReturn(java.util.Collections.emptyList());
		when(service.getMessage()).thenReturn(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* ======================= ACT ======================= */
		final java.util.List<String> retour = controller.rechercherTousString();
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

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
	 * {@link SousTypeProduitIController#MESSAGE_RECHERCHE_PAGINEE_REQUETE_NULL}</li>
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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);

		/* ======================= ACT ======================= */
		final levy.daniel.application.model.dto.pagination.ResultatPageDTO<OutputDTO> retour
			= controller.rechercherTousParPage(null);

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(controller.getMessage())
				.isEqualTo(
						SousTypeProduitIController
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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);
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
				.thenReturn(SousTypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

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
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);

		final levy.daniel.application.model.dto.pagination.TriSpecDTO triDTO
			= new levy.daniel.application.model.dto.pagination.TriSpecDTO(
					"sousTypeProduit",
					levy.daniel.application.model.dto.pagination.DirectionTriDTO.DESC);

		final levy.daniel.application.model.dto.pagination.RequetePageDTO requeteDTO
			= new levy.daniel.application.model.dto.pagination.RequetePageDTO(
					2,
					3,
					java.util.List.of(triDTO));

		final OutputDTO dto1 = new SousTypeProduitDTO.OutputDTO(1L, BAZAR, OUTILLAGE, null);
		final OutputDTO dto2 = new SousTypeProduitDTO.OutputDTO(2L, TOURISME, "guide", null);

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
				.thenReturn(SousTypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

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
				.extracting(SousTypeProduitDTO.OutputDTO::getIdSousTypeProduit)
				.containsExactly(1L, 2L);
		assertThat(retour.getContent())
				.extracting(SousTypeProduitDTO.OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, TOURISME);
		assertThat(retour.getContent())
				.extracting(SousTypeProduitDTO.OutputDTO::getSousTypeProduit)
				.containsExactly(OUTILLAGE, "guide");
		assertThat(controller.getMessage())
				.isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		verify(service, times(1)).rechercherTousParPage(
				org.mockito.ArgumentMatchers.argThat(requete ->
						requete != null
						&& requete.getPageNumber() == 1
						&& requete.getPageSize() == 3
						&& requete.getTris() != null
						&& requete.getTris().size() == 1
						&& "sousTypeProduit".equals(requete.getTris().get(0).getPropriete())
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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);
		final levy.daniel.application.model.dto.pagination.RequetePageDTO requeteDTO
			= new levy.daniel.application.model.dto.pagination.RequetePageDTO(1, 10);

		final IllegalStateException panneTechnique
			= new IllegalStateException(SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ SousTypeProduitICuService.TIRET_ESPACE
				+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

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
	 * {@link SousTypeProduitIController#MESSAGE_FIND_BY_LIBELLE_VUE_NULL}</li>
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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findByLibelle(null);

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(controller.getMessage())
				.isEqualTo(SousTypeProduitIController.MESSAGE_FIND_BY_LIBELLE_VUE_NULL);

		verifyNoInteractions(service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByLibelle(blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne
	 * {@link SousTypeProduitIController#MESSAGE_FIND_BY_LIBELLE_VUE_BLANK}</li>
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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findByLibelle(ESPACES);

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(controller.getMessage())
				.isEqualTo(SousTypeProduitIController.MESSAGE_FIND_BY_LIBELLE_VUE_BLANK);

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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);
		final String libelle = OUTILLAGE;

		final OutputDTO trouveA
			= new SousTypeProduitDTO.OutputDTO(1L, BAZAR, libelle, null);
		final OutputDTO trouveB
			= new SousTypeProduitDTO.OutputDTO(2L, TOURISME, libelle, null);

		when(service.findByLibelle(libelle))
				.thenReturn(java.util.List.of(trouveA, trouveB));
		when(service.getMessage())
				.thenReturn(SousTypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findByLibelle(libelle);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);
		assertThat(retour)
				.extracting(SousTypeProduitDTO.OutputDTO::getIdSousTypeProduit)
				.containsExactly(1L, 2L);
		assertThat(retour)
				.extracting(SousTypeProduitDTO.OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, TOURISME);
		assertThat(retour)
				.extracting(SousTypeProduitDTO.OutputDTO::getSousTypeProduit)
				.containsExactly(libelle, libelle);
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);
		final String libelle = "sous-type-produit-introuvable";
		final String messageService
			= SousTypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelle;

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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);
		final String libelle = OUTILLAGE;

		final IllegalStateException panneTechnique
			= new IllegalStateException(SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ SousTypeProduitICuService.TIRET_ESPACE
				+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

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
	 * {@link SousTypeProduitIController#MESSAGE_FIND_BY_LIBELLE_RAPIDE_VUE_NULL}</li>
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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findByLibelleRapide(null);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(
						SousTypeProduitIController
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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);
		final OutputDTO dto = new SousTypeProduitDTO.OutputDTO(1L, BAZAR, OUTILLAGE, null);
		final java.util.List<OutputDTO> trouves = java.util.List.of(dto);

		when(service.findByLibelleRapide(ESPACES)).thenReturn(trouves);
		when(service.getMessage()).thenReturn(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findByLibelleRapide(ESPACES);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(1);
		assertThat(retour)
				.extracting(SousTypeProduitDTO.OutputDTO::getSousTypeProduit)
				.containsExactly(OUTILLAGE);
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);
		final String contenu = "outi";

		final OutputDTO dto1 = new SousTypeProduitDTO.OutputDTO(1L, BAZAR, OUTILLAGE, null);
		final OutputDTO dto2 = new SousTypeProduitDTO.OutputDTO(2L, TOURISME, OUTILLAGE, null);
		final java.util.List<OutputDTO> trouves = java.util.List.of(dto1, dto2);

		when(service.findByLibelleRapide(contenu)).thenReturn(trouves);
		when(service.getMessage()).thenReturn(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findByLibelleRapide(contenu);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);
		assertThat(retour)
				.extracting(SousTypeProduitDTO.OutputDTO::getIdSousTypeProduit)
				.containsExactly(1L, 2L);
		assertThat(retour)
				.extracting(SousTypeProduitDTO.OutputDTO::getTypeProduit)
				.containsExactly(BAZAR, TOURISME);
		assertThat(retour)
				.extracting(SousTypeProduitDTO.OutputDTO::getSousTypeProduit)
				.containsExactly(OUTILLAGE, OUTILLAGE);
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_OK);

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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);
		final String contenu = "sous-type-produit-introuvable";

		when(service.findByLibelleRapide(contenu)).thenReturn(java.util.Collections.emptyList());
		when(service.getMessage()).thenReturn(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findByLibelleRapide(contenu);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message).isEqualTo(SousTypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

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
		final SousTypeProduitICuService service = mock(SousTypeProduitICuService.class);
		final SousTypeProduitWebController controller
			= new SousTypeProduitWebController(service);
		final String contenu = OUTILLAGE;

		final IllegalStateException panneTechnique
			= new IllegalStateException(SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= SousTypeProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ SousTypeProduitICuService.TIRET_ESPACE
				+ SousTypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

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

	
	
	// ---------------------- findByDTO(...) ----------------------------//

	
	
	// ----------------------- findById(...) ----------------------------//

	
	
	// ------------------------ update(...) -----------------------------//

	
	
	// ------------------------ delete(...) -----------------------------//

	
	
	// -------------------------- count() -------------------------------//

	
	
	// ------------------------ getMessage() ----------------------------//

	
	
} // FIN DE LA CLASSE SousTypeProduitWebControllerMockTest.----------------