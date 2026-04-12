/* ********************************************************************* */
/* ******************** TEST MOCKMVC CONTROLLER WEB ******************** */
/* ********************************************************************* */
package levy.daniel.application.controllers.metier.produittype.web;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import levy.daniel.application.controllers.metier.produittype.TypeProduitIController;
import levy.daniel.application.model.dto.produittype.TypeProduitDTO;
import levy.daniel.application.model.services.produittype.cu.TypeProduitICuService;

/**
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE TypeProduitWebControllerMockMvcTest.java :
 * </p>
 * <p>
 * Exemple de tests MockMvc ciblés du CONTROLLER ADAPTER WEB
 * {@link TypeProduitWebController}.
 * </p>
 * <p>
 * Cette classe ne remplace ni les tests Mockito du controller,
 * ni les tests d'intégration avec preuve BD.
 * Elle ajoute un petit verrouillage HTTP réel sur quelques endpoints critiques :
 * mapping Spring MVC, binding JSON,
 * corps absent sur {@code @RequestBody(required = false)}
 * et restitution HTTP de la réponse.
 * </p>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 10 avril 2026
 */
@WebMvcTest(controllers = TypeProduitWebController.class)
@ContextConfiguration(
		classes = TypeProduitWebControllerMockMvcTest.MockMvcBootConfiguration.class)
@ActiveProfiles({  "test-web-jpa" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TypeProduitWebControllerMockMvcTest {

	// *************************** CONSTANTES ******************************/

	/** Tag JUnit : tests MockMvc du controller web. */
	public static final String TAG = "controller-web-mockmvc";

	/** TypeProduit : "bazar". */
	public static final String BAZAR = "bazar";

	/** TypeProduit modifié : "tourisme". */
	public static final String TOURISME = "tourisme";

	/** "/typeproduit/message" */
	public static final String TYPE_PRODUIT_MESSAGE = "/typeproduit/message";

	// *************************** BEANS ***********************************/

	/** MockMvc Spring MVC. */
	@Autowired
	private MockMvc mockMvc;

	/** SERVICE UC mocké sous le nom réellement injecté par le controller. */
	@MockitoBean(name = "TypeProduitCuService")
	private TypeProduitICuService service;

	// ********************** CONFIGURATION DE TEST ***********************/

	/**
	 * <div>
	 * <p>Configuration Spring Boot minimale dédiée au test MockMvc.</p>
	 * </div>
	 */
	@SpringBootConfiguration
	@EnableAutoConfiguration
	@Import(TypeProduitWebController.class)
	public static class MockMvcBootConfiguration {
		/* configuration minimale de bootstrapping du test. */
	}

	// *************************** CONSTRUCTEUR ****************************/

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public TypeProduitWebControllerMockMvcTest() {
		super();
	}

	// *************************** METHODES *******************************/

	/**
	 * <div>
	 * <p>POST /typeproduit/creer sans body : le binding HTTP transmet {@code null}.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("POST /typeproduit/creer sans body : 200 + message local MESSAGE_CREER_VUE_NULL + aucune interaction service")
	public void testCreerSansBody() throws Exception {

		/* ======================= ACT ======================= */
		this.mockMvc.perform(post("/typeproduit/creer"))
				.andExpect(status().isOk())
				.andExpect(content().string(""));

		/* ===================== ASSERT ====================== */
		this.mockMvc.perform(get(TYPE_PRODUIT_MESSAGE))
				.andExpect(status().isOk())
				.andExpect(content().string(
						TypeProduitIController.MESSAGE_CREER_VUE_NULL));

		verifyNoInteractions(this.service);

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>GET /typeproduit/rechercherTous : smoke test du mapping GET et du rendu JSON.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("GET /typeproduit/rechercherTous : 200 + tableau JSON + message service")
	public void testRechercherTousHttpOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitDTO.OutputDTO dto
			= new TypeProduitDTO.OutputDTO(1L, BAZAR);

		when(this.service.rechercherTous()).thenReturn(List.of(dto));
		when(this.service.getMessage())
				.thenReturn(TypeProduitICuService.MESSAGE_RECHERCHE_OK);

		/* ======================= ACT ======================= */
		this.mockMvc.perform(get("/typeproduit/rechercherTous"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].idTypeProduit").value(1))
				.andExpect(jsonPath("$[0].typeProduit").value(BAZAR));

		/* ===================== ASSERT ====================== */
		this.mockMvc.perform(get(TYPE_PRODUIT_MESSAGE))
				.andExpect(status().isOk())
				.andExpect(content().string(
						TypeProduitICuService.MESSAGE_RECHERCHE_OK));

		verify(this.service, times(1)).rechercherTous();
		verify(this.service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>PUT /typeproduit : smoke test du mapping PUT racine et du binding JSON.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("PUT /typeproduit : 200 + binding JSON InputDTO + JSON OutputDTO + message service")
	public void testUpdateHttpOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		final TypeProduitDTO.OutputDTO output
			= new TypeProduitDTO.OutputDTO(7L, TOURISME);

		when(this.service.update(argThat(dto ->
				dto != null
				&& TOURISME.equals(dto.getTypeProduit()))))
				.thenReturn(output);
		when(this.service.getMessage())
				.thenReturn(TypeProduitICuService.MESSAGE_MODIF_OK + TOURISME);

		/* ======================= ACT ======================= */
		this.mockMvc.perform(
				put("/typeproduit")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "typeProduit": "tourisme"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.idTypeProduit").value(7))
				.andExpect(jsonPath("$.typeProduit").value(TOURISME));

		/* ===================== ASSERT ====================== */
		this.mockMvc.perform(get(TYPE_PRODUIT_MESSAGE))
				.andExpect(status().isOk())
				.andExpect(content().string(
						TypeProduitICuService.MESSAGE_MODIF_OK + TOURISME));

		verify(this.service, times(1)).update(argThat(dto ->
				dto != null
				&& TOURISME.equals(dto.getTypeProduit())));
		verify(this.service, times(1)).getMessage();

	} // __________________________________________________________________



	/**
	 * <div>
	 * <p>DELETE /typeproduit : smoke test du mapping DELETE racine et du binding JSON.</p>
	 * </div>
	 *
	 * @throws Exception
	 */
	@Test
	@Tag(TAG)
	@DisplayName("DELETE /typeproduit : 200 + binding JSON InputDTO + message service")
	public void testDeleteHttpOk() throws Exception {

		/* ===================== ARRANGE ===================== */
		doNothing().when(this.service)
				.delete(argThat(dto ->
						dto != null
						&& BAZAR.equals(dto.getTypeProduit())));
		when(this.service.getMessage())
				.thenReturn(TypeProduitICuService.MESSAGE_DELETE_OK + BAZAR);

		/* ======================= ACT ======================= */
		this.mockMvc.perform(
				delete("/typeproduit")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "typeProduit": "bazar"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(content().string(""));

		/* ===================== ASSERT ====================== */
		this.mockMvc.perform(get(TYPE_PRODUIT_MESSAGE))
				.andExpect(status().isOk())
				.andExpect(content().string(
						TypeProduitICuService.MESSAGE_DELETE_OK + BAZAR));

		verify(this.service, times(1)).delete(argThat(dto ->
				dto != null
				&& BAZAR.equals(dto.getTypeProduit())));
		verify(this.service, times(1)).getMessage();

	} // __________________________________________________________________



} // FIN DE LA CLASSE TypeProduitWebControllerMockMvcTest.----------------