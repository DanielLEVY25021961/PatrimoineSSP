package levy.daniel.application.model.dto.produittype;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.groups.Tuple;
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
 * <li>convertList(null) retourne null</li>
 * <li>convertList(...) conserve l'ordre de première apparition</li>
 * <li>convertList(...) ignore les éléments métier null</li>
 * <li>convertList(...) dédoublonne selon {@link ProduitDTO.OutputDTO#equals(Object)}</li>
 * <li>convertList(...) ne dédoublonne pas le cas critique id non null / id null</li>
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

	/** "tee-shirt pour homme" */
	public static final String TEE_SHIRT_HOMME = "tee-shirt pour homme";

	/** "sweat-shirt pour homme" */
	public static final String SWEAT_SHIRT_HOMME = "sweat-shirt pour homme";

	/** "blouson pour homme" */
	public static final String BLOUSON_HOMME = "blouson pour homme";

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

	/** "La liste convertie doit être null." */
	public static final String MSG_LIST_NULL = "La liste convertie doit être null.";

	/** "La liste convertie ne doit pas être null." */
	public static final String MSG_LIST_NOT_NULL = "La liste convertie ne doit pas être null.";

	/** "La liste convertie doit conserver l'ordre de première apparition." */
	public static final String MSG_ORDRE = "La liste convertie doit conserver l'ordre de première apparition.";

	/** "La liste convertie doit ignorer les éléments métier null." */
	public static final String MSG_NULL_ELEMENTS_IGNORES = "La liste convertie doit ignorer les éléments métier null.";

	/** "La liste convertie doit dédoublonner les DTO égaux." */
	public static final String MSG_DEDOUBLONNAGE = "La liste convertie doit dédoublonner les DTO égaux.";

	/** "Le cas critique id non null / id null ne doit pas être dédoublonné." */
	public static final String MSG_ID_NON_NULL_VS_NULL = "Le cas critique id non null / id null ne doit pas être dédoublonné.";

	/** id technique 101L. */
	public static final Long ID_101 = Long.valueOf(101L);

	/** id technique 202L. */
	public static final Long ID_202 = Long.valueOf(202L);

	/** id technique 303L. */
	public static final Long ID_303 = Long.valueOf(303L);

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

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>
	 * Vérifie la conversion nominale : id, typeProduit (grand-parent),
	 * sousTypeProduit (parent) et produit.
	 * </p>
	 *
	 * <p>
	 * Le scénario force ici un id non null afin de verrouiller réellement
	 * la recopie de l'identifiant.
	 * </p>
	 * </div>
	 */
	@Tag(TAG_METIER_TO_OUTPUT_DTO_BETON)
	@DisplayName("Conversion nominale : id + typeProduit + sousTypeProduit + produit")
	@Test
	public void testConvertNominal() {

		/* GIVEN */
		final ScenarioMetier scenario = ScenarioMetier.creerScenarioNominal();

		/* WHEN */
		final ProduitDTO.OutputDTO dto =
				ConvertisseurMetierToOutputDTOProduit
					.convert(scenario.produitChemiseManchesLongues);

		/* THEN */
		assertThat(dto)
			.as(MSG_DTO_NOT_NULL)
			.isNotNull();

		assertThat(dto.getIdProduit())
			.as(MSG_ID)
			.isEqualTo(ID_101);

		assertThat(dto.getTypeProduit())
			.as(MSG_TYPEPRODUIT)
			.isEqualTo(VETEMENT);

		assertThat(dto.getSousTypeProduit())
			.as(MSG_SOUS_TYPE_PRODUIT)
			.isEqualTo(VETEMENT_POUR_HOMME);

		assertThat(dto.getProduit())
			.as(MSG_PRODUIT)
			.isEqualTo(CHEMISES_MANCHES_LONGUES_HOMME);

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>
	 * Vérifie la tolérance quand le SousTypeProduit est null :
	 * le DTO retourne typeProduit null et sousTypeProduit null.
	 * </p>
	 * </div>
	 */
	@Tag(TAG_METIER_TO_OUTPUT_DTO)
	@DisplayName("Tolérance : sousTypeProduit null -> typeProduit null / sousTypeProduit null")
	@Test
	public void testConvertSousTypeProduitNull() {

		/* GIVEN */
		final Produit produit =
				new Produit(CHEMISES_MANCHES_LONGUES_HOMME, (SousTypeProduitI) null);

		/* WHEN */
		final ProduitDTO.OutputDTO dto =
				ConvertisseurMetierToOutputDTOProduit.convert(produit);

		/* THEN */
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

	} // __________________________________________________________________

	
	
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

		/* GIVEN */
		final SousTypeProduit stp = new SousTypeProduit(VETEMENT_POUR_HOMME);
		final Produit produit = new Produit(CHEMISES_MANCHES_LONGUES_HOMME, stp);

		/* WHEN */
		final ProduitDTO.OutputDTO dto =
				ConvertisseurMetierToOutputDTOProduit.convert(produit);

		/* THEN */
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

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>
	 * Vérifie que convertList(null) retourne null.
	 * </p>
	 * </div>
	 */
	@Tag(TAG_METIER_TO_OUTPUT_DTO)
	@DisplayName("convertList(null) -> null")
	@Test
	public void testConvertListNull() {

		final List<ProduitDTO.OutputDTO> dtoList =
				ConvertisseurMetierToOutputDTOProduit.convertList(null);

		assertThat(dtoList)
			.as(MSG_LIST_NULL)
			.isNull();

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>
	 * Vérifie que convertList(...) conserve l'ordre de première apparition
	 * des éléments uniques.
	 * </p>
	 * </div>
	 */
	@Tag(TAG_METIER_TO_OUTPUT_DTO_BETON)
	@DisplayName("convertList(...) conserve l'ordre de première apparition")
	@Test
	public void testConvertListOrdreConserve() {

		/* GIVEN */
		final SousTypeProduit stp = creerSousTypeProduitHomme();
		final List<Produit> produits = new ArrayList<Produit>();

		produits.add(new Produit(ID_202, TEE_SHIRT_HOMME, stp));
		produits.add(new Produit(ID_101, CHEMISES_MANCHES_LONGUES_HOMME, stp));
		produits.add(new Produit(ID_303, SWEAT_SHIRT_HOMME, stp));

		/* WHEN */
		final List<ProduitDTO.OutputDTO> dtoList =
				ConvertisseurMetierToOutputDTOProduit.convertList(produits);

		/* THEN */
		assertThat(dtoList)
			.as(MSG_LIST_NOT_NULL)
			.isNotNull();

		assertThat(extraireTuples(dtoList))
			.as(MSG_ORDRE)
			.containsExactly(
					tuple(ID_202, VETEMENT, VETEMENT_POUR_HOMME, TEE_SHIRT_HOMME),
					tuple(ID_101, VETEMENT, VETEMENT_POUR_HOMME, CHEMISES_MANCHES_LONGUES_HOMME),
					tuple(ID_303, VETEMENT, VETEMENT_POUR_HOMME, SWEAT_SHIRT_HOMME));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>
	 * Vérifie que convertList(...) ignore les éléments métier null.
	 * </p>
	 * </div>
	 */
	@Tag(TAG_METIER_TO_OUTPUT_DTO)
	@DisplayName("convertList(...) ignore les éléments métier null")
	@Test
	public void testConvertListIgnoreElementsNull() {

		/* GIVEN */
		final SousTypeProduit stp = creerSousTypeProduitHomme();
		final List<Produit> produits = new ArrayList<Produit>();

		produits.add(null);
		produits.add(new Produit(ID_101, CHEMISES_MANCHES_LONGUES_HOMME, stp));
		produits.add(null);
		produits.add(new Produit(ID_202, TEE_SHIRT_HOMME, stp));

		/* WHEN */
		final List<ProduitDTO.OutputDTO> dtoList =
				ConvertisseurMetierToOutputDTOProduit.convertList(produits);

		/* THEN */
		assertThat(dtoList)
			.as(MSG_LIST_NOT_NULL)
			.isNotNull();

		assertThat(extraireTuples(dtoList))
			.as(MSG_NULL_ELEMENTS_IGNORES)
			.containsExactly(
					tuple(ID_101, VETEMENT, VETEMENT_POUR_HOMME, CHEMISES_MANCHES_LONGUES_HOMME),
					tuple(ID_202, VETEMENT, VETEMENT_POUR_HOMME, TEE_SHIRT_HOMME));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>
	 * Vérifie le dédoublonnage voulu quand deux objets métier distincts
	 * convertissent vers des DTO égaux.
	 * </p>
	 *
	 * <p>
	 * Ici, les deux objets métier portent la même clé métier
	 * et aucun id technique.
	 * </p>
	 * </div>
	 */
	@Tag(TAG_METIER_TO_OUTPUT_DTO)
	@DisplayName("convertList(...) dédoublonne les DTO égaux quand les ids sont nuls")
	@Test
	public void testConvertListDedoublonneQuandIdsNuls() {

		/* GIVEN */
		final SousTypeProduit stp = creerSousTypeProduitHomme();
		final List<Produit> produits = new ArrayList<Produit>();

		final Produit produit1 =
				new Produit((Long) null, CHEMISES_MANCHES_LONGUES_HOMME, stp);
		final Produit produit2 =
				new Produit((Long) null, CHEMISES_MANCHES_LONGUES_HOMME, stp);

		produits.add(produit1);
		produits.add(produit2);

		/* WHEN */
		final List<ProduitDTO.OutputDTO> dtoList =
				ConvertisseurMetierToOutputDTOProduit.convertList(produits);

		/* THEN */
		assertThat(dtoList)
			.as(MSG_LIST_NOT_NULL)
			.isNotNull();

		assertThat(extraireTuples(dtoList))
			.as(MSG_DEDOUBLONNAGE)
			.containsExactly(
					tuple(null, VETEMENT, VETEMENT_POUR_HOMME, CHEMISES_MANCHES_LONGUES_HOMME));

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>
	 * Vérifie le cas critique sanctuarisé :
	 * un DTO avec id non null et un DTO avec id null ne doivent pas être
	 * dédoublonnés, même si la clé métier est identique.
	 * </p>
	 * </div>
	 */
	@Tag(TAG_METIER_TO_OUTPUT_DTO)
	@DisplayName("convertList(...) conserve le cas critique id non null / id null")
	@Test
	public void testConvertListConserveIdNonNullVsIdNull() {

		/* GIVEN */
		final SousTypeProduit stp = creerSousTypeProduitHomme();
		final List<Produit> produits = new ArrayList<Produit>();

		final Produit produitAvecId =
				new Produit(ID_101, CHEMISES_MANCHES_LONGUES_HOMME, stp);
		final Produit produitSansId =
				new Produit((Long) null, CHEMISES_MANCHES_LONGUES_HOMME, stp);

		produits.add(produitAvecId);
		produits.add(produitSansId);

		/* WHEN */
		final List<ProduitDTO.OutputDTO> dtoList =
				ConvertisseurMetierToOutputDTOProduit.convertList(produits);

		/* THEN */
		assertThat(dtoList)
			.as(MSG_LIST_NOT_NULL)
			.isNotNull();

		assertThat(extraireTuples(dtoList))
			.as(MSG_ID_NON_NULL_VS_NULL)
			.containsExactly(
					tuple(ID_101, VETEMENT, VETEMENT_POUR_HOMME, CHEMISES_MANCHES_LONGUES_HOMME),
					tuple(null, VETEMENT, VETEMENT_POUR_HOMME, CHEMISES_MANCHES_LONGUES_HOMME));

	} // __________________________________________________________________

	
	
	/* ============================== OUTILS ============================== */

	/**
	 * <div>
	 * <p>
	 * Extrait des tuples lisibles pour verrouiller les assertions
	 * sans dépendre du equals/hashCode de la liste elle-même.
	 * </p>
	 * </div>
	 *
	 * @param pDtoList : liste de DTO
	 * @return liste de tuples
	 */
	private static List<Tuple> extraireTuples(
			final List<ProduitDTO.OutputDTO> pDtoList) {

		final List<Tuple> result = new ArrayList<Tuple>();

		if (pDtoList == null) {
			return result;
		}

		for (final ProduitDTO.OutputDTO dto : pDtoList) {
			result.add(tuple(
					dto.getIdProduit(),
					dto.getTypeProduit(),
					dto.getSousTypeProduit(),
					dto.getProduit()));
		}

		return result;

	} // __________________________________________________________________

	
	
	/**
	 * <div>
	 * <p>
	 * Crée un sous-type produit nominal rattaché à son type produit parent.
	 * </p>
	 * </div>
	 *
	 * @return SousTypeProduit
	 */
	private static SousTypeProduit creerSousTypeProduitHomme() {

		final TypeProduit tp = new TypeProduit(VETEMENT);
		final List<ProduitI> produits = new ArrayList<ProduitI>();

		return new SousTypeProduit(VETEMENT_POUR_HOMME, tp, produits);

	} // __________________________________________________________________

	
	
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

		/**
		 * <div>
		 * <p>Constructeur privé du scénario.</p>
		 * </div>
		 *
		 * @param pProduit : produit du scénario
		 */
		private ScenarioMetier(final Produit pProduit) {
			this.produitChemiseManchesLongues = pProduit;
		} // ______________________________________________________________

		
		
		/**
		 * <div>
		 * <p>
		 * Crée le scénario nominal avec un id non null,
		 * afin de verrouiller réellement la recopie d'identifiant.
		 * </p>
		 * </div>
		 *
		 * @return ScenarioMetier
		 */
		private static ScenarioMetier creerScenarioNominal() {

			final TypeProduit tp = new TypeProduit(VETEMENT);
			final List<ProduitI> produits = new ArrayList<ProduitI>();
			final SousTypeProduit stp =
					new SousTypeProduit(VETEMENT_POUR_HOMME, tp, produits);

			final Produit produit =
					new Produit(ID_101, CHEMISES_MANCHES_LONGUES_HOMME, stp);

			return new ScenarioMetier(produit);

		} // ______________________________________________________________

		
		
	} // Fin de la CLASSE ScenarioMetier.__________________________________
	
	

} // FIN DE LA CLASSE ConvertisseurMetierToOutputDTOProduitTest.-----------