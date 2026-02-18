package levy.daniel.application.model.dto.produittype;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 * 
 * <div>
 * <p style="font-weight:bold;">CLASSE ProduitDTOTest.java :</p>
 * <p>Tests béton pour ProduitDTO.</p>
 * 
 * <p>
 * Cette classe teste la classe java pur DTO : 
 * <span style="font-weight:bold;">ProduitDTO</span> 
 * </p>
 * 
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 17 janvier 2026
 */
@Tag("DTO")
public class ProduitDTOTest {

	// ************************ATTRIBUTS************************************/

	/**
	 * "Vetement"
	 */
	public static final String VETEMENT = "Vetement";

	/**
	 * "Peche"
	 */
	public static final String PECHE = "Peche";

	/**
	 * "Outillage"
	 */
	public static final String OUTILLAGE = "Outillage";

	/**
	 * "Homme"
	 */
	public static final String HOMME = "Homme";

	/**
	 * "Canne"
	 */
	public static final String CANNE = "Canne";

	/**
	 * "Chemise"
	 */
	public static final String CHEMISE = "Chemise";

	/**
	 * "Pantalon"
	 */
	public static final String PANTALON = "Pantalon";

	/* ------------------------------------------------------------------ */

	/**
	 * <style>p, ul, li {line-height : 1em;}</style>
	 * <div>
	 * <p>LOG : Logger : </p>
	 * <p>Logger pour Log4j (utilisant org.apache.logging.log4j).</p>
	 * <p>dépendances : </p>
	 * <ul>
	 * <li><code>org.apache.logging.log4j.Logger</code></li>
	 * <li><code>org.apache.logging.log4j.LogManager</code></li>
	 * </ul>
	 * </div>
	 */
	private static final Logger LOG = LogManager
			.getLogger(ProduitDTOTest.class);

	// *************************METHODES************************************/

	/**
	 * <div>
	 * <p>constructeur d'arité nulle par défaut.</p>
	 * </div>
	 */
	public ProduitDTOTest() {
		super();
	}

	
	
	/**
	 *  .
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("ProduitDTO : classe utilitaire final + constructeur privé")
	public void testStructureClasseUtilitaire() throws Exception {

		assertThat(Modifier.isFinal(ProduitDTO.class.getModifiers())).isTrue();

		final Constructor<?>[] ctors = ProduitDTO.class.getDeclaredConstructors();
		assertThat(ctors).isNotNull();
		assertThat(ctors.length).isEqualTo(1);

		final Constructor<?> ctor = ctors[0];
		assertThat(Modifier.isPrivate(ctor.getModifiers())).isTrue();

		ctor.setAccessible(true); // NOPMD by danyl on 17/01/2026 22:10
		final Object instance = ctor.newInstance();
		assertThat(instance).isNotNull();
	}

	
	
	/**
	 *  .
	 *
	 */
	@Test
	@DisplayName("ProduitDTO.InputDTO : constructeurs + getters/setters + equals/hashCode/toString")
	public void testInputDTOBeton() {

		final ProduitDTO.InputDTO dtoNull = new ProduitDTO.InputDTO();
		assertThat(dtoNull.getTypeProduit()).isNull();
		assertThat(dtoNull.getSousTypeProduit()).isNull();
		assertThat(dtoNull.getProduit()).isNull();

		final ProduitDTO.InputDTO dto1 = new ProduitDTO.InputDTO(VETEMENT, HOMME, CHEMISE);
		assertThat(dto1.getTypeProduit()).isEqualTo(VETEMENT);
		assertThat(dto1.getSousTypeProduit()).isEqualTo(HOMME);
		assertThat(dto1.getProduit()).isEqualTo(CHEMISE);

		dto1.setTypeProduit(PECHE);
		dto1.setSousTypeProduit(CANNE);
		dto1.setProduit(PANTALON);

		assertThat(dto1.getTypeProduit()).isEqualTo(PECHE);
		assertThat(dto1.getSousTypeProduit()).isEqualTo(CANNE);
		assertThat(dto1.getProduit()).isEqualTo(PANTALON);

		final ProduitDTO.InputDTO dto2 = new ProduitDTO.InputDTO(PECHE, CANNE, PANTALON);

		assertThat(dto1).isEqualTo(dto2);
		assertThat(dto2).isEqualTo(dto1);
		assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

		assertThat(dto1).isNotEqualTo(null);
		assertThat(dto1).isNotEqualTo(PECHE);

		final String toString = dto1.toString();
		assertThat(toString).contains("InputDTO");
		assertThat(toString).contains(PECHE);
		assertThat(toString).contains(CANNE);
		assertThat(toString).contains(PANTALON);
	}

	
	
	/**
	 *  .
	 *
	 */
	@Test
	@DisplayName("ProduitDTO.OutputDTO : equals/hashCode ID-first puis fallback (produit+sousTypeProduit+typeProduit) + toString")
	public void testOutputDTOBeton() {

		// --- Cas ID-first : même ID => equals true même si contenu diff
		final ProduitDTO.OutputDTO o1 =
				new ProduitDTO.OutputDTO(10L, VETEMENT, HOMME, CHEMISE);

		final ProduitDTO.OutputDTO o2 =
				new ProduitDTO.OutputDTO(10L, PECHE, CANNE, PANTALON);

		assertThat(o1).isEqualTo(o2);
		assertThat(o2).isEqualTo(o1);
		assertThat(o1.hashCode()).isEqualTo(o2.hashCode());

		// --- Cas fallback : IDs null => compare (produit + sousTypeProduit + typeProduit)
		final ProduitDTO.OutputDTO o3 =
				new ProduitDTO.OutputDTO(null, VETEMENT, HOMME, CHEMISE);

		final ProduitDTO.OutputDTO o4 =
				new ProduitDTO.OutputDTO(null, VETEMENT, HOMME, CHEMISE);

		assertThat(o3).isEqualTo(o4);
		assertThat(o3.hashCode()).isEqualTo(o4.hashCode());

		final ProduitDTO.OutputDTO o5 =
				new ProduitDTO.OutputDTO(null, VETEMENT, HOMME, "Veste");

		assertThat(o3).isNotEqualTo(o5);

		final ProduitDTO.OutputDTO o6 =
				new ProduitDTO.OutputDTO(null, PECHE, HOMME, CHEMISE);

		assertThat(o3).isNotEqualTo(o6);

		final ProduitDTO.OutputDTO o7 =
				new ProduitDTO.OutputDTO(null, VETEMENT, "Femme", CHEMISE);

		assertThat(o3).isNotEqualTo(o7);

		// --- Mutabilité via setters
		final ProduitDTO.OutputDTO o8 = new ProduitDTO.OutputDTO();
		o8.setIdProduit(20L);
		o8.setTypeProduit(OUTILLAGE);
		o8.setSousTypeProduit("Scie");
		o8.setProduit("Scie circulaire");

		assertThat(o8.getIdProduit()).isEqualTo(20L);
		assertThat(o8.getTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(o8.getSousTypeProduit()).isEqualTo("Scie");
		assertThat(o8.getProduit()).isEqualTo("Scie circulaire");

		// --- toString
		final String toString = o8.toString();
		assertThat(toString).contains("OutputDTO");
		assertThat(toString).contains(OUTILLAGE);
		assertThat(toString).contains("Scie");
		assertThat(toString).contains("Scie circulaire");

		// --- null safety equals
		assertThat(o8).isNotEqualTo(null);
		assertThat(o8).isNotEqualTo(OUTILLAGE);
	}

}
