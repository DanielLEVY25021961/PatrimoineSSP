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
import levy.daniel.application.model.metier.produittype.SousTypeProduitI;
import levy.daniel.application.model.metier.produittype.TypeProduit;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">
 * Test JUnit du convertisseur {@link ConvertisseurMetierToOutputDTOProduit} :
 * conversion d'un {@link Produit} métier vers {@link ProduitDTO.OutputDTO}.
 * </p>
 *
 * <p>
 * Vérifie :
 * </p>
 * <ul>
 * <li>convert(null) retourne null</li>
 * <li>extraction du TypeProduit (grand-parent) (lazy-safe)</li>
 * <li>extraction du SousTypeProduit parent (lazy-safe)</li>
 * <li>tolérance aux graphes partiels (stp null / tp null)</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 18 janvier 2026
 */
public class ConvertisseurMetierToOutputDTOProduitTest {

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

	/** "Le DTO retourné doit être null." */
	public static final String MSG_DTO_NULL = "Le DTO retourné doit être null.";

	/** "Le DTO retourné ne doit pas être null." */
	public static final String MSG_DTO_NOT_NULL = "Le DTO retourné ne doit pas être null.";

	/** "L'id du Produit doit être recopié." */
	public static final String MSG_ID = "L'id du Produit doit être recopié.";

	/** "Le libellé du TypeProduit (grand-parent) doit être extrait." */
	public static final String MSG_TYPEPRODUIT = "Le libellé du TypeProduit (grand-parent) doit être extrait.";

	/** "Le libellé du SousTypeProduit parent doit être extrait." */
	public static final String MSG_SOUS_TYPE_PRODUIT = "Le libellé du SousTypeProduit parent doit être extrait.";

	/** "Le libellé du Produit doit être recopié." */
	public static final String MSG_PRODUIT = "Le libellé du Produit doit être recopié.";

	/** "Le TypeProduit doit être null quand l'objet est incomplet." */
	public static final String MSG_TYPEPRODUIT_NULL = "Le TypeProduit doit être null quand l'objet est incomplet.";

	/** "Le SousTypeProduit doit être null quand l'objet est incomplet." */
	public static final String MSG_SOUS_TYPE_PRODUIT_NULL = "Le SousTypeProduit doit être null quand l'objet est incomplet.";


	// *************************METHODES************************************/

	/**
	 * <div>
	 * <p>
	 * CONSTRUCTEUR D'ARITE NULLE.
	 * </p>
	 * </div>
	 */
	public ConvertisseurMetierToOutputDTOProduitTest() {
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

		final ProduitDTO.OutputDTO dto =
				ConvertisseurMetierToOutputDTOProduit.convert(null);

		assertThat(dto)
			.as(MSG_DTO_NULL)
			.isNull();
	}

	/**
	 * <div>
	 * <p>
	 * Vérifie la conversion nominale : id, typeProduit (grand-parent), sousTypeProduit (parent) et produit.
	 * </p>
	 * </div>
	 */
	@Tag(TAG_METIER_TO_OUTPUT_DTO_BETON)
	@DisplayName("Conversion nominale : id + typeProduit + sousTypeProduit + produit")
	@Test
	public void testConvertNominal() {

		// GIVEN
		final ScenarioMetier scenario = ScenarioMetier.creerScenario();

		// WHEN
		final ProduitDTO.OutputDTO dto =
				ConvertisseurMetierToOutputDTOProduit.convert(scenario.produitChemiseManchesLongues);

		// THEN
		assertThat(dto)
			.as(MSG_DTO_NOT_NULL)
			.isNotNull();

		assertThat(dto.getIdProduit())
			.as(MSG_ID)
			.isEqualTo(scenario.produitChemiseManchesLongues.getIdProduit());

		assertThat(dto.getTypeProduit())
			.as(MSG_TYPEPRODUIT)
			.isEqualTo(VETEMENT);

		assertThat(dto.getSousTypeProduit())
			.as(MSG_SOUS_TYPE_PRODUIT)
			.isEqualTo(VETEMENT_POUR_HOMME);

		assertThat(dto.getProduit())
			.as(MSG_PRODUIT)
			.isEqualTo(CHEMISES_MANCHES_LONGUES_HOMME);
	}

	/**
	 * <div>
	 * <p>
	 * Vérifie la tolérance quand le SousTypeProduit est null : le DTO retourne typeProduit null et sousTypeProduit null.
	 * </p>
	 * </div>
	 */
	@Tag(TAG_METIER_TO_OUTPUT_DTO)
	@DisplayName("Tolérance : sousTypeProduit null -> typeProduit null / sousTypeProduit null")
	@Test
	public void testConvertSousTypeProduitNull() {

		// GIVEN
		final Produit produit = new Produit(CHEMISES_MANCHES_LONGUES_HOMME, (SousTypeProduitI) null);

		// WHEN
		final ProduitDTO.OutputDTO dto =
				ConvertisseurMetierToOutputDTOProduit.convert(produit);

		// THEN
		assertThat(dto)
			.as(MSG_DTO_NOT_NULL)
			.isNotNull();

		assertThat(dto.getSousTypeProduit())
			.as(MSG_SOUS_TYPE_PRODUIT_NULL)
			.isNull();

		assertThat(dto.getTypeProduit())
			.as(MSG_TYPEPRODUIT_NULL)
			.isNull();

		assertThat(dto.getProduit())
			.as(MSG_PRODUIT)
			.isEqualTo(CHEMISES_MANCHES_LONGUES_HOMME);
	}

	/**
	 * <div>
	 * <p>
	 * Vérifie la tolérance quand le TypeProduit du SousTypeProduit est null :
	 * le DTO retourne typeProduit null mais sousTypeProduit non null.
	 * </p>
	 * </div>
	 */
	@Tag(TAG_METIER_TO_OUTPUT_DTO)
	@DisplayName("Tolérance : typeProduit null -> typeProduit null / sousTypeProduit présent")
	@Test
	public void testConvertTypeProduitNullDansSousTypeProduit() {

		// GIVEN
		final SousTypeProduit stp = new SousTypeProduit(VETEMENT_POUR_HOMME);
		final Produit produit = new Produit(CHEMISES_MANCHES_LONGUES_HOMME, stp);

		// WHEN
		final ProduitDTO.OutputDTO dto =
				ConvertisseurMetierToOutputDTOProduit.convert(produit);

		// THEN
		assertThat(dto)
			.as(MSG_DTO_NOT_NULL)
			.isNotNull();

		assertThat(dto.getSousTypeProduit())
			.as(MSG_SOUS_TYPE_PRODUIT)
			.isEqualTo(VETEMENT_POUR_HOMME);

		assertThat(dto.getTypeProduit())
			.as(MSG_TYPEPRODUIT_NULL)
			.isNull();

		assertThat(dto.getProduit())
			.as(MSG_PRODUIT)
			.isEqualTo(CHEMISES_MANCHES_LONGUES_HOMME);
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

		/** Produit : "chemise manches longues pour homme". */
		private final Produit produitChemiseManchesLongues;

		/** ScenarioMetier */
		private ScenarioMetier(final Produit pProduit) {
			this.produitChemiseManchesLongues = pProduit;
		}

		/** Crée le scénario. */
		private static ScenarioMetier creerScenario() {
			final TypeProduit tp = new TypeProduit(VETEMENT);
			final List<ProduitI> produits = new ArrayList<ProduitI>();
			final SousTypeProduit stp = new SousTypeProduit(VETEMENT_POUR_HOMME, tp, produits);

			final Produit produit = new Produit(CHEMISES_MANCHES_LONGUES_HOMME, stp);
			produits.add(produit);

			return new ScenarioMetier(produit);
		}
		
	}
}
