package levy.daniel.application.model.dto.produittype;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

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
 * <p style="font-weight:bold;">CLASSE SousTypeProduitDTOTest.java :</p>
 * <p>Tests béton pour SousTypeProduitDTO.</p>
 * 
 * <p>
 * Cette classe teste la classe java pur DTO : 
 * <span style="font-weight:bold;">SousTypeProduitDTO</span> 
 * </p>
 * 
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 17 janvier 2026
 */
@Tag("DTO")
public class SousTypeProduitDTOTest {

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
	 * "Entree"
	 */
	public static final String ENTREE = "Entree";
	
	/**
	 * "Homme"
	 */
	public static final String HOMME = "Homme";
	
	/**
	 * "Canne"
	 */
	public static final String CANNE = "Canne";
	
	
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
			.getLogger(SousTypeProduitDTOTest.class);

	// *************************METHODES************************************/
	
	/**
	 * <div>
	 * <p>constructeur d'arité nulle par défaut.</p>
	 * </div>
	 */
	public SousTypeProduitDTOTest() {
		super();
	}

	
	
	/**
	 *  .
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("SousTypeProduitDTO : classe utilitaire final + constructeur privé")
	public void testStructureClasseUtilitaire() throws Exception {

		assertThat(Modifier.isFinal(SousTypeProduitDTO.class.getModifiers())).isTrue();

		final Constructor<?>[] ctors = SousTypeProduitDTO.class.getDeclaredConstructors();
		assertThat(ctors).isNotNull();
		assertThat(ctors.length).isEqualTo(1);

		final Constructor<?> ctor = ctors[0];
		assertThat(Modifier.isPrivate(ctor.getModifiers())).isTrue();

		ctor.setAccessible(true); // NOPMD by danyl on 17/01/2026 21:53
		final Object instance = ctor.newInstance();
		assertThat(instance).isNotNull();
	}

	
	
	/**
	 *  .
	 *
	 */
	@Test
	@DisplayName("SousTypeProduitDTO.InputDTO : constructeurs + getters/setters + equals/hashCode/toString")
	public void testInputDTOBeton() {

		final SousTypeProduitDTO.InputDTO dtoNull = new SousTypeProduitDTO.InputDTO();
		assertThat(dtoNull.getTypeProduit()).isNull();
		assertThat(dtoNull.getSousTypeProduit()).isNull();

		final SousTypeProduitDTO.InputDTO dto1 = new SousTypeProduitDTO.InputDTO(VETEMENT, HOMME);
		assertThat(dto1.getTypeProduit()).isEqualTo(VETEMENT);
		assertThat(dto1.getSousTypeProduit()).isEqualTo(HOMME);

		dto1.setTypeProduit(PECHE);
		dto1.setSousTypeProduit(CANNE);

		assertThat(dto1.getTypeProduit()).isEqualTo(PECHE);
		assertThat(dto1.getSousTypeProduit()).isEqualTo(CANNE);

		final SousTypeProduitDTO.InputDTO dto2 = new SousTypeProduitDTO.InputDTO(PECHE, CANNE);

		assertThat(dto1).isEqualTo(dto2);
		assertThat(dto2).isEqualTo(dto1);
		assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

		assertThat(dto1).isNotEqualTo(null);
		assertThat(dto1).isNotEqualTo(PECHE);

		final String toString = dto1.toString();
		assertThat(toString).contains("InputDTO");
		assertThat(toString).contains(PECHE);
		assertThat(toString).contains(CANNE);
	}

	
	
	/**
	 *  .
	 *
	 */
	@Test
	@DisplayName("SousTypeProduitDTO.OutputDTO : equals/hashCode ID-first puis fallback (typeProduit+sousTypeProduit) + liste produits")
	public void testOutputDTOBeton() {

		final List<String> produits1 = new ArrayList<String>();
		produits1.add("Chemise");
		produits1.add("Pantalon");

		final List<String> produits2 = new ArrayList<String>();
		produits2.add("Veste");

		// --- Cas ID-first : même ID => equals true même si contenu diff
		final SousTypeProduitDTO.OutputDTO o1 =
				new SousTypeProduitDTO.OutputDTO(10L, VETEMENT, HOMME, produits1);

		final SousTypeProduitDTO.OutputDTO o2 =
				new SousTypeProduitDTO.OutputDTO(10L, PECHE, CANNE, produits2);

		assertThat(o1).isEqualTo(o2);
		assertThat(o2).isEqualTo(o1);
		assertThat(o1.hashCode()).isEqualTo(o2.hashCode());

		// --- Cas fallback : IDs null => compare (sousTypeProduit + typeProduit)
		final SousTypeProduitDTO.OutputDTO o3 =
				new SousTypeProduitDTO.OutputDTO(null, VETEMENT, HOMME, produits1);

		final SousTypeProduitDTO.OutputDTO o4 =
				new SousTypeProduitDTO.OutputDTO(null, VETEMENT, HOMME, produits2);

		assertThat(o3).isEqualTo(o4);
		assertThat(o3.hashCode()).isEqualTo(o4.hashCode());

		final SousTypeProduitDTO.OutputDTO o5 =
				new SousTypeProduitDTO.OutputDTO(null, VETEMENT, "Femme", produits1);

		assertThat(o3).isNotEqualTo(o5);

		final SousTypeProduitDTO.OutputDTO o6 =
				new SousTypeProduitDTO.OutputDTO(null, PECHE, HOMME, produits1);

		assertThat(o3).isNotEqualTo(o6);

		// --- Mutabilité via setters
		final SousTypeProduitDTO.OutputDTO o7 = new SousTypeProduitDTO.OutputDTO();
		o7.setIdSousTypeProduit(20L);
		o7.setTypeProduit("Restauration");
		o7.setSousTypeProduit(ENTREE);
		o7.setProduits(produits1);

		assertThat(o7.getIdSousTypeProduit()).isEqualTo(20L);
		assertThat(o7.getTypeProduit()).isEqualTo("Restauration");
		assertThat(o7.getSousTypeProduit()).isEqualTo(ENTREE);
		assertThat(o7.getProduits()).isSameAs(produits1);

		// --- toString
		final String toString = o7.toString();
		assertThat(toString).contains("OutputDTO");
		assertThat(toString).contains("Restauration");
		assertThat(toString).contains(ENTREE);

		// --- null safety equals
		assertThat(o7).isNotEqualTo(null);
		assertThat(o7).isNotEqualTo(ENTREE);
	}

}
