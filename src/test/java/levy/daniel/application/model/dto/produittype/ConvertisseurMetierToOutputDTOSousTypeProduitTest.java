package levy.daniel.application.model.dto.produittype;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import levy.daniel.application.model.metier.produittype.Produit;
import levy.daniel.application.model.metier.produittype.ProduitI;
import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.TypeProduit;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">
 * Test JUnit du convertisseur {@link ConvertisseurMetierToOutputDTOSousTypeProduit} :
 * conversion d'un {@link SousTypeProduit} métier vers {@link SousTypeProduitDTO.OutputDTO}.
 * </p>
 *
 * <p>
 * Vérifie :
 * </p>
 * <ul>
 * <li>convert(null) retourne null</li>
 * <li>extraction du TypeProduit parent (lazy-safe)</li>
 * <li>extraction des Produits (libellés) petits-enfants</li>
 * <li>tolérance aux listes null / éléments null</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 18 janvier 2026
 */
public class ConvertisseurMetierToOutputDTOSousTypeProduitTest {

	// ************************ATTRIBUTS************************************/

	/** Tag JUnit : "MetierToOutputDTO". */
	public static final String TAG_METIER_TO_OUTPUT_DTO = "MetierToOutputDTO";

	/** Tag JUnit : "MetierToOutputDTO-Beton". */
	public static final String TAG_METIER_TO_OUTPUT_DTO_BETON = "MetierToOutputDTO-Beton";

	/** "vêtement" */
	public static final String VETEMENT = "vêtement";

	/** "vêtement pour homme" */
	public static final String VETEMENT_POUR_HOMME = "vêtement pour homme";

	/** "chemise manches longues pour homme" */
	public static final String CHEMISES_MANCHES_LONGUES_HOMME = "chemise manches longues pour homme";

	/** "tee-shirt pour homme" */
	public static final String TEE_SHIRT_HOMME = "tee-shirt pour homme";

	/** "sweat-shirt pour homme" */
	public static final String SWEAT_SHIRT_HOMME = "sweat-shirt pour homme";

	/** "Le DTO retourné doit être null." */
	public static final String MSG_DTO_NULL = "Le DTO retourné doit être null.";

	/** "Le DTO retourné ne doit pas être null." */
	public static final String MSG_DTO_NOT_NULL = "Le DTO retourné ne doit pas être null.";

	/** "L'id du SousTypeProduit doit être recopié." */
	public static final String MSG_ID = "L'id du SousTypeProduit doit être recopié.";

	/** "Le libellé du TypeProduit parent doit être extrait." */
	public static final String MSG_TYPEPRODUIT = "Le libellé du TypeProduit parent doit être extrait.";

	/** "Le libellé du SousTypeProduit doit être recopié." */
	public static final String MSG_SOUS_TYPE_PRODUIT = "Le libellé du SousTypeProduit doit être recopié.";

	/** "La liste des produits ne doit jamais être null." */
	public static final String MSG_PRODUITS_NOT_NULL = "La liste des produits ne doit jamais être null.";

	/** "La liste des produits doit contenir les libellés." */
	public static final String MSG_PRODUITS_CONTENU = "La liste des produits doit contenir les libellés.";

	/** "La liste des produits doit être vide quand la liste métier est null." */
	public static final String MSG_PRODUITS_VIDE = "La liste des produits doit être vide quand la liste métier est null.";


	// *************************METHODES************************************/

	/**
	 * <div>
	 * <p>
	 * CONSTRUCTEUR D'ARITE NULLE.
	 * </p>
	 * </div>
	 */
	public ConvertisseurMetierToOutputDTOSousTypeProduitTest() {
		super();
	} // Fin du CONSTRUCTEUR D'ARITE NULLE.________________________________


	/* ============================== TESTS ============================== */

	/**
	 * <div>
	 * <p>
	 * Vérifie que convert(null) retourne null.
	 * </p>
	 * </div>
	 */
	@Tag(TAG_METIER_TO_OUTPUT_DTO)
	@DisplayName("convert(null) -> null")
	@Test
	public void testConvertNull() {

		final SousTypeProduitDTO.OutputDTO dto =
				ConvertisseurMetierToOutputDTOSousTypeProduit.convert(null);

		assertThat(dto)
			.as(MSG_DTO_NULL)
			.isNull();
	}

	/**
	 * <div>
	 * <p>
	 * Vérifie la conversion nominale : id, typeProduit parent, sousTypeProduit et produits.
	 * </p>
	 * </div>
	 */
	@Tag(TAG_METIER_TO_OUTPUT_DTO_BETON)
	@DisplayName("Conversion nominale : id + typeProduit + sousTypeProduit + produits")
	@Test
	public void testConvertNominal() {

		// GIVEN
		final ScenarioMetier scenario = ScenarioMetier.creerScenario();

		// WHEN
		final SousTypeProduitDTO.OutputDTO dto =
				ConvertisseurMetierToOutputDTOSousTypeProduit.convert(scenario.stpHomme);

		// THEN
		assertThat(dto)
			.as(MSG_DTO_NOT_NULL)
			.isNotNull();

		assertThat(dto.getIdSousTypeProduit())
			.as(MSG_ID)
			.isEqualTo(scenario.stpHomme.getIdSousTypeProduit());

		assertThat(dto.getTypeProduit())
			.as(MSG_TYPEPRODUIT)
			.isEqualTo(VETEMENT);

		assertThat(dto.getSousTypeProduit())
			.as(MSG_SOUS_TYPE_PRODUIT)
			.isEqualTo(VETEMENT_POUR_HOMME);

		assertThat(dto.getProduits())
			.as(MSG_PRODUITS_NOT_NULL)
			.isNotNull();

		assertThat(dto.getProduits())
			.as(MSG_PRODUITS_CONTENU)
			.containsExactly(CHEMISES_MANCHES_LONGUES_HOMME, TEE_SHIRT_HOMME, SWEAT_SHIRT_HOMME);
	}

	/**
	 * <div>
	 * <p>
	 * Vérifie la tolérance quand la liste des produits est null : le DTO retourne une liste vide.
	 * </p>
	 * </div>
	 */
	@Tag(TAG_METIER_TO_OUTPUT_DTO)
	@DisplayName("Tolérance : produits null -> liste vide")
	@Test
	public void testConvertProduitsNull() {

		// GIVEN
		final TypeProduit tp = new TypeProduit(VETEMENT);
		final SousTypeProduit stp = new SousTypeProduit(VETEMENT_POUR_HOMME, tp, null);

		// WHEN
		final SousTypeProduitDTO.OutputDTO dto =
				ConvertisseurMetierToOutputDTOSousTypeProduit.convert(stp);

		// THEN
		assertThat(dto)
			.as(MSG_DTO_NOT_NULL)
			.isNotNull();

		assertThat(dto.getProduits())
			.as(MSG_PRODUITS_VIDE)
			.isNotNull()
			.isEmpty();
	}

	/**
	 * <div>
	 * <p>
	 * Vérifie que les éléments null dans la liste métier sont ignorés.
	 * </p>
	 * </div>
	 */
	@Tag(TAG_METIER_TO_OUTPUT_DTO)
	@DisplayName("Tolérance : éléments null -> ignorés")
	@Test
	public void testConvertIgnoreElementsNull() {

		// GIVEN
		final TypeProduit tp = new TypeProduit(VETEMENT);

		// Créer une liste MODIFIABLE de ProduitI, puis la passer au constructeur.
		final List<ProduitI> produits = new ArrayList<ProduitI>();
		final SousTypeProduit stp = new SousTypeProduit(VETEMENT_POUR_HOMME, tp, produits);

		// Alimenter la liste via la référence PRODUITS (pas via stp.getProduits()).
		produits.add(null);
		produits.add(new Produit(CHEMISES_MANCHES_LONGUES_HOMME, stp));
		produits.add(new Produit(null, stp));
		produits.add(new Produit(TEE_SHIRT_HOMME, stp));

		// WHEN
		final SousTypeProduitDTO.OutputDTO dto =
				ConvertisseurMetierToOutputDTOSousTypeProduit.convert(stp);

		// THEN
		assertThat(dto)
			.as(MSG_DTO_NOT_NULL)
			.isNotNull();

		assertThat(dto.getProduits())
			.as(MSG_PRODUITS_CONTENU)
			.containsExactly(CHEMISES_MANCHES_LONGUES_HOMME, TEE_SHIRT_HOMME);
	}


	/* ============================== SCENARIO ============================== */

	/**
	 * <div>
	 * <p>Scenario simple pour construire un graphe métier.</p>
	 * </div>
	 *
	 * @author Daniel Lévy
	 * @version 1.0
	 * @since 18 janvier 2026
	 */
	private static final class ScenarioMetier {

		/** SousTypeProduit : "vêtement pour homme". */
		private final SousTypeProduit stpHomme;

		/** ScenarioMetier. */
		private ScenarioMetier(final SousTypeProduit pStp) {
			this.stpHomme = pStp;
		}

		
		/**
		 * <div>
		 * <p>crée le scénario.</p>
		 * </div>
		 *
		 * @return ScenarioMetier
		 */
		private static ScenarioMetier creerScenario() {

			final TypeProduit tp = new TypeProduit(VETEMENT);
			final List<ProduitI> produits = new ArrayList<ProduitI>();
			final SousTypeProduit stp = new SousTypeProduit(VETEMENT_POUR_HOMME, tp, produits);

			produits.add(new Produit(CHEMISES_MANCHES_LONGUES_HOMME, stp));
			produits.add(new Produit(TEE_SHIRT_HOMME, stp));
			produits.add(new Produit(SWEAT_SHIRT_HOMME, stp));

			return new ScenarioMetier(stp);
		}
	}
}
