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
 * <span style="font-weight:bold;">ProduitDTO</span>.
 * </p>
 * 
 * <p>
 * Elle vérifie :
 * </p>
 * <ul>
 * <li>la structure utilitaire de la classe englobante ;</li>
 * <li>le comportement de <code>InputDTO</code> ;</li>
 * <li>le comportement de <code>OutputDTO</code> ;</li>
 * <li>la cohérence de <code>equals/hashCode/toString</code> ;</li>
 * <li>le cas critique d'inégalité quand un seul ID est présent.</li>
 * </ul>
 * 
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 17 janvier 2026
 */
@Tag("DTO")
public class ProduitDTOTest {

	/* ************************ATTRIBUTS************************************/

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
	 * "Femme"
	 */
	public static final String FEMME = "Femme";

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

	/**
	 * "Veste"
	 */
	public static final String VESTE = "Veste";

	/**
	 * "Scie"
	 */
	public static final String SCIE = "Scie";

	/**
	 * "Scie circulaire"
	 */
	public static final String SCIE_CIRCULAIRE = "Scie circulaire";

	/* ------------------------------------------------------------------ */

	/**
	 * <style>p, ul, li {line-height : 1em;}</style>
	 * <div>
	 * <p>LOG : Logger :</p>
	 * <p>Logger pour Log4j (utilisant org.apache.logging.log4j).</p>
	 * <p>dépendances :</p>
	 * <ul>
	 * <li><code>org.apache.logging.log4j.Logger</code></li>
	 * <li><code>org.apache.logging.log4j.LogManager</code></li>
	 * </ul>
	 * </div>
	 */
	private static final Logger LOG = LogManager
			.getLogger(ProduitDTOTest.class);

	/* *************************METHODES************************************/

	/**
	 * <div>
	 * <p>Constructeur d'arité nulle par défaut.</p>
	 * </div>
	 */
	public ProduitDTOTest() {
		super();
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>Vérifie que <code>ProduitDTO</code> est bien une classe utilitaire :</p>
	 * <ul>
	 * <li>la classe est <code>final</code> ;</li>
	 * <li>elle expose un unique constructeur ;</li>
	 * <li>ce constructeur est <code>private</code>.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception en cas d'échec de réflexion.
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

		/*
		 * Vérifie qu'il reste possible d'instancier la classe par réflexion
		 * pour couvrir le constructeur privé de la classe utilitaire.
		 */
		ctor.setAccessible(true); // NOPMD by danyl on 17/01/2026 22:10
		final Object instance = ctor.newInstance();
		assertThat(instance).isNotNull();
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>Vérifie le comportement nominal de <code>InputDTO</code> :</p>
	 * <ul>
	 * <li>constructeur par défaut ;</li>
	 * <li>constructeur complet ;</li>
	 * <li>getter / setter ;</li>
	 * <li>cohérence de <code>equals/hashCode</code> ;</li>
	 * <li>présence des informations utiles dans <code>toString()</code>.</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("ProduitDTO.InputDTO : constructeurs + getters/setters + equals/hashCode/toString")
	public void testInputDTOBeton() {

		final ProduitDTO.InputDTO dtoNull = new ProduitDTO.InputDTO();
		assertThat(dtoNull.getTypeProduit()).isNull();
		assertThat(dtoNull.getSousTypeProduit()).isNull();
		assertThat(dtoNull.getProduit()).isNull();

		final ProduitDTO.InputDTO dto1 =
				new ProduitDTO.InputDTO(VETEMENT, HOMME, CHEMISE);
		assertThat(dto1.getTypeProduit()).isEqualTo(VETEMENT);
		assertThat(dto1.getSousTypeProduit()).isEqualTo(HOMME);
		assertThat(dto1.getProduit()).isEqualTo(CHEMISE);

		/*
		 * Vérifie la mutabilité nominale du DTO d'entrée
		 * au travers des setters publics.
		 */
		dto1.setTypeProduit(PECHE);
		dto1.setSousTypeProduit(CANNE);
		dto1.setProduit(PANTALON);

		assertThat(dto1.getTypeProduit()).isEqualTo(PECHE);
		assertThat(dto1.getSousTypeProduit()).isEqualTo(CANNE);
		assertThat(dto1.getProduit()).isEqualTo(PANTALON);

		final ProduitDTO.InputDTO dto2 =
				new ProduitDTO.InputDTO(PECHE, CANNE, PANTALON);

		/*
		 * Vérifie la cohérence du couple equals/hashCode
		 * lorsque les trois champs métier sont identiques.
		 */
		assertThat(dto1).isEqualTo(dto2);
		assertThat(dto2).isEqualTo(dto1);
		assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

		/*
		 * Vérifie la robustesse minimale de equals(...).
		 */
		assertThat(dto1).isNotEqualTo(null);
		assertThat(dto1).isNotEqualTo(PECHE);

		/*
		 * Vérifie la présence des données utiles dans toString().
		 */
		final String toString = dto1.toString();
		assertThat(toString).contains("InputDTO");
		assertThat(toString).contains(PECHE);
		assertThat(toString).contains(CANNE);
		assertThat(toString).contains(PANTALON);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>Vérifie le comportement nominal de <code>OutputDTO</code> :</p>
	 * <ul>
	 * <li>priorité de l'ID dans <code>equals/hashCode</code> ;</li>
	 * <li>fallback métier sur <code>(produit, sousTypeProduit, typeProduit)</code>
	 * quand les deux IDs sont nuls ;</li>
	 * <li>inégalité si le fallback métier ne matche pas ;</li>
	 * <li>mutabilité via les setters ;</li>
	 * <li>présence des informations utiles dans <code>toString()</code>.</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("ProduitDTO.OutputDTO : equals/hashCode ID-first puis fallback (produit+sousTypeProduit+typeProduit) + toString")
	public void testOutputDTOBeton() {

		/*
		 * Cas ID-first :
		 * même ID => égalité vraie,
		 * même si les autres champs diffèrent.
		 */
		final ProduitDTO.OutputDTO o1 =
				new ProduitDTO.OutputDTO(10L, VETEMENT, HOMME, CHEMISE);

		final ProduitDTO.OutputDTO o2 =
				new ProduitDTO.OutputDTO(10L, PECHE, CANNE, PANTALON);

		assertThat(o1).isEqualTo(o2);
		assertThat(o2).isEqualTo(o1);
		assertThat(o1.hashCode()).isEqualTo(o2.hashCode());

		/*
		 * Cas fallback métier :
		 * les deux IDs sont nuls,
		 * l'égalité repose donc sur le triplet
		 * (produit, sousTypeProduit, typeProduit).
		 */
		final ProduitDTO.OutputDTO o3 =
				new ProduitDTO.OutputDTO(null, VETEMENT, HOMME, CHEMISE);

		final ProduitDTO.OutputDTO o4 =
				new ProduitDTO.OutputDTO(null, VETEMENT, HOMME, CHEMISE);

		assertThat(o3).isEqualTo(o4);
		assertThat(o4).isEqualTo(o3);
		assertThat(o3.hashCode()).isEqualTo(o4.hashCode());

		/*
		 * Toujours en fallback métier :
		 * un produit différent doit produire une inégalité.
		 */
		final ProduitDTO.OutputDTO o5 =
				new ProduitDTO.OutputDTO(null, VETEMENT, HOMME, VESTE);

		assertThat(o3).isNotEqualTo(o5);
		assertThat(o5).isNotEqualTo(o3);

		/*
		 * Toujours en fallback métier :
		 * un sous-type différent doit produire une inégalité.
		 */
		final ProduitDTO.OutputDTO o6 =
				new ProduitDTO.OutputDTO(null, PECHE, HOMME, CHEMISE);

		assertThat(o3).isNotEqualTo(o6);
		assertThat(o6).isNotEqualTo(o3);

		/*
		 * Toujours en fallback métier :
		 * un type parent différent doit produire une inégalité.
		 */
		final ProduitDTO.OutputDTO o7 =
				new ProduitDTO.OutputDTO(null, VETEMENT, FEMME, CHEMISE);

		assertThat(o3).isNotEqualTo(o7);
		assertThat(o7).isNotEqualTo(o3);

		/*
		 * Vérifie la mutabilité via les setters.
		 */
		final ProduitDTO.OutputDTO o8 = new ProduitDTO.OutputDTO();
		o8.setIdProduit(20L);
		o8.setTypeProduit(OUTILLAGE);
		o8.setSousTypeProduit(SCIE);
		o8.setProduit(SCIE_CIRCULAIRE);

		assertThat(o8.getIdProduit()).isEqualTo(20L);
		assertThat(o8.getTypeProduit()).isEqualTo(OUTILLAGE);
		assertThat(o8.getSousTypeProduit()).isEqualTo(SCIE);
		assertThat(o8.getProduit()).isEqualTo(SCIE_CIRCULAIRE);

		/*
		 * Vérifie la forme utile de toString().
		 */
		final String toString = o8.toString();
		assertThat(toString).contains("OutputDTO");
		assertThat(toString).contains(OUTILLAGE);
		assertThat(toString).contains(SCIE);
		assertThat(toString).contains(SCIE_CIRCULAIRE);

		/*
		 * Robustesse minimale de equals(...).
		 */
		assertThat(o8).isNotEqualTo(null);
		assertThat(o8).isNotEqualTo(OUTILLAGE);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>Vérifie le cas critique corrigé dans <code>OutputDTO.equals(...)</code> :</p>
	 * <ul>
	 * <li>si un objet possède un ID non nul ;</li>
	 * <li>et l'autre un ID nul ;</li>
	 * <li>alors les deux objets doivent être inégaux,</li>
	 * <li>même si <code>typeProduit</code>, <code>sousTypeProduit</code>
	 * et <code>produit</code> sont identiques.</li>
	 * </ul>
	 * 
	 * <p>
	 * Ce test verrouille la cohérence entre
	 * <code>equals(...)</code> et <code>hashCode()</code>.
	 * </p>
	 * </div>
	 */
	@Test
	@DisplayName("ProduitDTO.OutputDTO : id non null vs id null => inégalité même si typeProduit, sousTypeProduit et produit identiques")
	public void testOutputDTOIdNonNullVsIdNullDoitEtreInegal() {

		/*
		 * Même identité métier apparente,
		 * mais asymétrie de présence d'ID :
		 * o1 a un ID technique,
		 * o2 n'en a pas.
		 */
		final ProduitDTO.OutputDTO o1 =
				new ProduitDTO.OutputDTO(10L, VETEMENT, HOMME, CHEMISE);

		final ProduitDTO.OutputDTO o2 =
				new ProduitDTO.OutputDTO(null, VETEMENT, HOMME, CHEMISE);

		/*
		 * Le contrat attendu est l'inégalité stricte
		 * pour préserver la cohérence avec hashCode().
		 */
		assertThat(o1).isNotEqualTo(o2);
		assertThat(o2).isNotEqualTo(o1);
		
	} // __________________________________________________________________
	
	

}