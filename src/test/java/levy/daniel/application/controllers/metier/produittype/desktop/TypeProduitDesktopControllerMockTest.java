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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);

		when(service.rechercherTous()).thenReturn(java.util.Collections.emptyList());
		when(service.getMessage()).thenReturn(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.rechercherTous();
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		verify(service, times(1)).rechercherTous();
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________
	

	
	// ------------------ rechercherTousString() -------------------------//
	
	
	
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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);

		when(service.rechercherTousString())
				.thenReturn(java.util.List.of(OUTILLAGE));
		when(service.getMessage())
				.thenReturn(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* ======================= ACT ======================= */
		final java.util.List<String> retour = controller.rechercherTousString();
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).containsExactly(OUTILLAGE);
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final IllegalStateException panneTechnique
			= new IllegalStateException(TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ TypeProduitICuService.TIRET_ESPACE
				+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

		when(service.rechercherTousString()).thenThrow(panneTechnique);
		when(service.getMessage()).thenReturn(messageService);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.rechercherTousString())
				.isSameAs(panneTechnique);

		assertThat(controller.getMessage()).isEqualTo(messageService);

		verify(service, times(1)).rechercherTousString();
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________

	

	// --------------- rechercherTousParPage(...) -----------------------//

	
	/**
	 * <div>
	 * <p>rechercherTousParPage(null) : contrôle de surface bénin côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link TypeProduitIController#MESSAGE_RECHERCHE_PAGINEE_REQUETE_NULL}</li>
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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);

		/* ======================= ACT ======================= */
		final levy.daniel.application.model.dto.pagination.ResultatPageDTO<OutputDTO> retour
			= controller.rechercherTousParPage(null);

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(controller.getMessage())
				.isEqualTo(
						TypeProduitIController
							.MESSAGE_RECHERCHE_PAGINEE_REQUETE_NULL);

		verifyNoInteractions(service);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>rechercherTousParPage(défaut) : scénario nominal avec pagination DTO par défaut.</p>
	 * <ul>
	 * <li>délègue au service avec page interne 0-based</li>
	 * <li>retourne un {@link levy.daniel.application.model.dto.pagination.ResultatPageDTO}
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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
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
				.thenReturn(TypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

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
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);

		final levy.daniel.application.model.dto.pagination.TriSpecDTO triDTO
			= new levy.daniel.application.model.dto.pagination.TriSpecDTO(
					"typeProduit",
					levy.daniel.application.model.dto.pagination.DirectionTriDTO.DESC);

		final levy.daniel.application.model.dto.pagination.RequetePageDTO requeteDTO
			= new levy.daniel.application.model.dto.pagination.RequetePageDTO(
					2,
					3,
					java.util.List.of(triDTO));

		final OutputDTO dto1 = new TypeProduitDTO.OutputDTO(1L, OUTILLAGE, null);
		final OutputDTO dto2 = new TypeProduitDTO.OutputDTO(2L, "tourisme", null);

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
				.thenReturn(TypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

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
				.extracting(TypeProduitDTO.OutputDTO::getIdTypeProduit)
				.containsExactly(1L, 2L);
		assertThat(retour.getContent())
				.extracting(TypeProduitDTO.OutputDTO::getTypeProduit)
				.containsExactly(OUTILLAGE, "tourisme");
		assertThat(controller.getMessage())
				.isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_PAGINEE_OK);

		verify(service, times(1)).rechercherTousParPage(
				org.mockito.ArgumentMatchers.argThat(requete ->
						requete != null
						&& requete.getPageNumber() == 1
						&& requete.getPageSize() == 3
						&& requete.getTris() != null
						&& requete.getTris().size() == 1
						&& "typeProduit".equals(requete.getTris().get(0).getPropriete())
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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final levy.daniel.application.model.dto.pagination.RequetePageDTO requeteDTO
			= new levy.daniel.application.model.dto.pagination.RequetePageDTO(1, 10);

		final IllegalStateException panneTechnique
			= new IllegalStateException(TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ TypeProduitICuService.TIRET_ESPACE
				+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

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
	

	
	// --------------------- findByLibelle(...) -------------------------//
	
	
	
	/**
	 * <div>
	 * <p>findByLibelle(null) : contrôle de surface bénin côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link TypeProduitIController#MESSAGE_FIND_BY_LIBELLE_VUE_NULL}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(null) : retourne null, message local, aucune interaction service")
	public void testFindByLibelleNull() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.findByLibelle(null);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitIController.MESSAGE_FIND_BY_LIBELLE_VUE_NULL);

		verifyNoInteractions(service);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findByLibelle(blank) : contrôle de surface applicatif côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link TypeProduitIController#MESSAGE_FIND_BY_LIBELLE_VUE_BLANK}</li>
	 * <li>n'interagit jamais avec le service</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("findByLibelle(blank) : retourne null, message local, aucune interaction service")
	public void testFindByLibelleBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.findByLibelle(ESPACES);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitIController.MESSAGE_FIND_BY_LIBELLE_VUE_BLANK);

		verifyNoInteractions(service);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findByLibelle(ok) : scénario nominal complet.</p>
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
	@DisplayName("findByLibelle(ok) : délégation service + OutputDTO + message service mémorisé")
	public void testFindByLibelleOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final String libelle = OUTILLAGE;
		final OutputDTO trouve = new TypeProduitDTO.OutputDTO(1L, libelle, null);

		when(service.findByLibelle(libelle)).thenReturn(trouve);
		when(service.getMessage()).thenReturn(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.findByLibelle(libelle);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdTypeProduit()).isEqualTo(1L);
		assertThat(retour.getTypeProduit()).isEqualTo(libelle);
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		verify(service, times(1)).findByLibelle(libelle);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>findByLibelle(absent) : scénario nominal sans résultat.</p>
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
	@DisplayName("findByLibelle(absent) : retourne null + message service mémorisé")
	public void testFindByLibelleAbsent() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final String libelle = "type-produit-introuvable";
		final String messageService
			= TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelle;

		when(service.findByLibelle(libelle)).thenReturn(null);
		when(service.getMessage()).thenReturn(messageService);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.findByLibelle(libelle);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final String libelle = OUTILLAGE;

		final IllegalStateException panneTechnique
			= new IllegalStateException(TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ TypeProduitICuService.TIRET_ESPACE
				+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

		when(service.findByLibelle(libelle)).thenThrow(panneTechnique);
		when(service.getMessage()).thenReturn(messageService);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.findByLibelle(libelle))
				.isSameAs(panneTechnique);

		assertThat(controller.getMessage()).isEqualTo(messageService);

		verify(service, times(1)).findByLibelle(libelle);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________
	

	
	// ------------------ findByLibelleRapide(...) ----------------------//
	
	
	
	/**
	 * <div>
	 * <p>findByLibelleRapide(null) : contrôle de surface bénin côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link TypeProduitIController#MESSAGE_FIND_BY_LIBELLE_RAPIDE_VUE_NULL}</li>
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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findByLibelleRapide(null);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(
						TypeProduitIController
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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final OutputDTO dto = new TypeProduitDTO.OutputDTO(1L, OUTILLAGE, null);
		final java.util.List<OutputDTO> trouves = java.util.List.of(dto);

		when(service.findByLibelleRapide(ESPACES)).thenReturn(trouves);
		when(service.getMessage()).thenReturn(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findByLibelleRapide(ESPACES);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(1);
		assertThat(retour)
				.extracting(TypeProduitDTO.OutputDTO::getTypeProduit)
				.containsExactly(OUTILLAGE);
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final String contenu = "outi";

		final OutputDTO dto1 = new TypeProduitDTO.OutputDTO(1L, OUTILLAGE, null);
		final OutputDTO dto2 = new TypeProduitDTO.OutputDTO(2L, "outils divers", null);
		final java.util.List<OutputDTO> trouves = java.util.List.of(dto1, dto2);

		when(service.findByLibelleRapide(contenu)).thenReturn(trouves);
		when(service.getMessage()).thenReturn(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findByLibelleRapide(contenu);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).hasSize(2);
		assertThat(retour)
				.extracting(TypeProduitDTO.OutputDTO::getIdTypeProduit)
				.containsExactly(1L, 2L);
		assertThat(retour)
				.extracting(TypeProduitDTO.OutputDTO::getTypeProduit)
				.containsExactly(OUTILLAGE, "outils divers");
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final String contenu = "type-produit-introuvable";

		when(service.findByLibelleRapide(contenu)).thenReturn(java.util.Collections.emptyList());
		when(service.getMessage()).thenReturn(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

		/* ======================= ACT ======================= */
		final java.util.List<OutputDTO> retour = controller.findByLibelleRapide(contenu);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour).isEmpty();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_RECHERCHE_VIDE);

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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final String contenu = OUTILLAGE;

		final IllegalStateException panneTechnique
			= new IllegalStateException(TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ TypeProduitICuService.TIRET_ESPACE
				+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

		when(service.findByLibelleRapide(contenu)).thenThrow(panneTechnique);
		when(service.getMessage()).thenReturn(messageService);

		/* =================== ACT & ASSERT ================== */
		assertThatThrownBy(() -> controller.findByLibelleRapide(contenu))
				.isSameAs(panneTechnique);

		assertThat(controller.getMessage()).isEqualTo(messageService);

		verify(service, times(1)).findByLibelleRapide(contenu);
		verify(service, times(1)).getMessage();

	} // __________________________________________________________________	


	
	// ---------------------- findByDTO(...) ----------------------------//
	
	
	
	/**
	 * <div>
	 * <p>findByDTO(null) : contrôle de surface bénin côté controller.</p>
	 * <ul>
	 * <li>retourne {@code null}</li>
	 * <li>positionne {@link TypeProduitIController#MESSAGE_FIND_BY_DTO_VUE_NULL}</li>
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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.findByDTO(null);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitIController.MESSAGE_FIND_BY_DTO_VUE_NULL);

		verifyNoInteractions(service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>findByDTO(blank) : le controller délègue au service.</p>
	 * <ul>
	 * <li>ne bloque pas localement un InputDTO portant un libellé blank</li>
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
	@DisplayName("findByDTO(blank) : délégation service + null + message service mémorisé")
	public void testFindByDTOBlank() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final InputDTO inputDTO = new TypeProduitDTO.InputDTO(ESPACES);

		when(service.findByDTO(inputDTO)).thenReturn(null);
		when(service.getMessage()).thenReturn(TypeProduitICuService.MESSAGE_PARAM_BLANK);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.findByDTO(inputDTO);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_PARAM_BLANK);

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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final InputDTO inputDTO = new TypeProduitDTO.InputDTO(OUTILLAGE);
		final OutputDTO trouve = new TypeProduitDTO.OutputDTO(1L, OUTILLAGE, null);

		when(service.findByDTO(inputDTO)).thenReturn(trouve);
		when(service.getMessage()).thenReturn(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.findByDTO(inputDTO);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdTypeProduit()).isEqualTo(1L);
		assertThat(retour.getTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final String libelle = "type-produit-dto-introuvable";
		final InputDTO inputDTO = new TypeProduitDTO.InputDTO(libelle);
		final String messageService
			= TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + libelle;

		when(service.findByDTO(inputDTO)).thenReturn(null);
		when(service.getMessage()).thenReturn(messageService);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.findByDTO(inputDTO);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message).isEqualTo(messageService);

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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final InputDTO inputDTO = new TypeProduitDTO.InputDTO(OUTILLAGE);

		final IllegalStateException panneTechnique
			= new IllegalStateException(TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ TypeProduitICuService.TIRET_ESPACE
				+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

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
	 * <li>positionne {@link TypeProduitIController#MESSAGE_FIND_BY_ID_VUE_NULL}</li>
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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.findById(null);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNull();
		assertThat(message)
				.isEqualTo(TypeProduitIController.MESSAGE_FIND_BY_ID_VUE_NULL);

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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final Long id = 1L;
		final OutputDTO trouve = new TypeProduitDTO.OutputDTO(id, OUTILLAGE, null);

		when(service.findById(id)).thenReturn(trouve);
		when(service.getMessage()).thenReturn(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

		/* ======================= ACT ======================= */
		final OutputDTO retour = controller.findById(id);
		final String message = controller.getMessage();

		/* ===================== ASSERT ====================== */
		assertThat(retour).isNotNull();
		assertThat(retour.getIdTypeProduit()).isEqualTo(id);
		assertThat(retour.getTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(message).isEqualTo(TypeProduitICuService.MESSAGE_SUCCES_RECHERCHE);

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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final Long id = 999_999_999L;
		final String messageService
			= TypeProduitICuService.MESSAGE_OBJ_INTROUVABLE + id;

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
		final TypeProduitICuService service = mock(TypeProduitICuService.class);
		final TypeProduitDesktopController controller
			= new TypeProduitDesktopController(service);
		final Long id = 1L;

		final IllegalStateException panneTechnique
			= new IllegalStateException(TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE);
		final String messageService
			= TypeProduitICuService.KO_TECHNIQUE_RECHERCHE
				+ TypeProduitICuService.TIRET_ESPACE
				+ TypeProduitICuService.MSG_ERREUR_NON_SPECIFIEE;

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


	
	// ------------------------ delete(...) -----------------------------//


	
	// -------------------------- count() -------------------------------//


	
	// ------------------------ getMessage() ----------------------------//
	
	
									
}