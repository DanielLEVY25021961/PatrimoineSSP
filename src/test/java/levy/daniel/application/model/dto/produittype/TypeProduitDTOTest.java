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
 * <p style="font-weight:bold;">CLASSE TypeProduitDTOTest.java :</p>
 * <p>Tests béton pour TypeProduitDTO.</p>
 * 
 * <p>
 * Cette classe teste la classe java pur DTO : 
 * <span style="font-weight:bold;">TypeProduitDTO</span> 
 * </p>
 * 
 * </div>
 * 
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 17 janvier 2026
 */
@Tag("DTO")
public class TypeProduitDTOTest {

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
			.getLogger(TypeProduitDTOTest.class);

	// *************************METHODES************************************/
	
	/**
	 * <div>
	 * <p>constructeur d'arité nulle par défaut.</p>
	 * </div>
	 */
	public TypeProduitDTOTest() {
		super();
	}

	
	
	/**
	 *  .
	 *
	 * @throws Exception
	 */
	@Test
	@DisplayName("TypeProduitDTO : classe utilitaire final + constructeur privé")
	public void testStructureClasseUtilitaire() throws Exception {

		assertThat(Modifier.isFinal(TypeProduitDTO.class.getModifiers())).isTrue();

		final Constructor<?>[] ctors = TypeProduitDTO.class.getDeclaredConstructors();
		assertThat(ctors).isNotNull();
		assertThat(ctors.length).isEqualTo(1);

		final Constructor<?> ctor = ctors[0];
		assertThat(Modifier.isPrivate(ctor.getModifiers())).isTrue();

		ctor.setAccessible(true); // NOPMD by danyl on 17/01/2026 21:41
		final Object instance = ctor.newInstance();
		assertThat(instance).isNotNull();
	}

	
	
	/**
	 *  .
	 *
	 */
	@Test
	@DisplayName("TypeProduitDTO.InputDTO : constructeurs + getters/setters + equals/hashCode/toString")
	public void testInputDTO() {

		final TypeProduitDTO.InputDTO dtoNull = new TypeProduitDTO.InputDTO();
		assertThat(dtoNull.getTypeProduit()).isNull();

		final TypeProduitDTO.InputDTO dto1 = new TypeProduitDTO.InputDTO(VETEMENT);
		assertThat(dto1.getTypeProduit()).isEqualTo(VETEMENT);

		dto1.setTypeProduit(PECHE);
		assertThat(dto1.getTypeProduit()).isEqualTo(PECHE);

		final TypeProduitDTO.InputDTO dto2 = new TypeProduitDTO.InputDTO(PECHE);

		assertThat(dto1).isEqualTo(dto2);
		assertThat(dto2).isEqualTo(dto1);
		assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

		assertThat(dto1).isNotEqualTo(null);
		assertThat(dto1).isNotEqualTo(PECHE);

		final String toString = dto1.toString();
		assertThat(toString).contains("InputDTO");
		assertThat(toString).contains(PECHE);
	}

	
	
	/**
	 *  .
	 *
	 */
	@Test
	@DisplayName("TypeProduitDTO.OutputDTO : equals/hashCode ID-first puis fallback typeProduit + toString + liste")
	public void testOutputDTOBeton() {

		final List<String> stp1 = new ArrayList<String>();
		stp1.add("Homme");
		stp1.add("Femme");

		final List<String> stp2 = new ArrayList<String>();
		stp2.add("Enfant");

		// --- Cas ID-first (même ID => equals true même si contenu diff)
		final TypeProduitDTO.OutputDTO o1 = new TypeProduitDTO.OutputDTO(1L, VETEMENT, stp1);
		final TypeProduitDTO.OutputDTO o2 = new TypeProduitDTO.OutputDTO(1L, PECHE, stp2);

		assertThat(o1).isEqualTo(o2);
		assertThat(o2).isEqualTo(o1);
		assertThat(o1.hashCode()).isEqualTo(o2.hashCode());

		// --- Cas ID non null vs ID null : doit retomber sur fallback
		final TypeProduitDTO.OutputDTO o3 = new TypeProduitDTO.OutputDTO(null, VETEMENT, stp1);
		final TypeProduitDTO.OutputDTO o4 = new TypeProduitDTO.OutputDTO(null, VETEMENT, stp2);

		assertThat(o3).isEqualTo(o4);
		assertThat(o3.hashCode()).isEqualTo(o4.hashCode());

		final TypeProduitDTO.OutputDTO o5 = new TypeProduitDTO.OutputDTO(null, PECHE, stp1);
		assertThat(o3).isNotEqualTo(o5);

		// --- Mutabilité via setters
		final TypeProduitDTO.OutputDTO o6 = new TypeProduitDTO.OutputDTO();
		o6.setIdTypeProduit(2L);
		o6.setTypeProduit(OUTILLAGE);
		o6.setSousTypeProduits(stp1);

		assertThat(o6.getIdTypeProduit()).isEqualTo(2L);
		assertThat(o6.getTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(o6.getSousTypeProduits()).isSameAs(stp1);

		// --- toString
		final String toString = o6.toString();
		assertThat(toString).contains("OutputDTO");
		assertThat(toString).contains(OUTILLAGE);

		// --- null safety equals
		assertThat(o6).isNotEqualTo(null);
		assertThat(o6).isNotEqualTo(OUTILLAGE);
	}

}
