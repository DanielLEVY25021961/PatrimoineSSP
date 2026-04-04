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
 * <span style="font-weight:bold;">SousTypeProduitDTO</span>.
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
public class SousTypeProduitDTOTest {

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

	/**
	 * "Restauration"
	 */
	public static final String RESTAURATION = "Restauration";

	/**
	 * "Femme"
	 */
	public static final String FEMME = "Femme";

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
			.getLogger(SousTypeProduitDTOTest.class);

	/* *************************METHODES************************************/

	/**
	 * <div>
	 * <p>Constructeur d'arité nulle par défaut.</p>
	 * </div>
	 */
	public SousTypeProduitDTOTest() {
		super();
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>Vérifie que <code>SousTypeProduitDTO</code> est bien une classe
	 * utilitaire :</p>
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
	@DisplayName("SousTypeProduitDTO : classe utilitaire final + constructeur privé")
	public void testStructureClasseUtilitaire() throws Exception {

		assertThat(Modifier.isFinal(SousTypeProduitDTO.class.getModifiers())).isTrue();

		final Constructor<?>[] ctors = SousTypeProduitDTO.class.getDeclaredConstructors();
		assertThat(ctors).isNotNull();
		assertThat(ctors.length).isEqualTo(1);

		final Constructor<?> ctor = ctors[0];
		assertThat(Modifier.isPrivate(ctor.getModifiers())).isTrue();

		/*
		 * Vérifie qu'il reste possible d'instancier la classe par réflexion
		 * pour couvrir le constructeur privé de la classe utilitaire.
		 */
		ctor.setAccessible(true); // NOPMD by danyl on 17/01/2026 21:53
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
	@DisplayName("SousTypeProduitDTO.InputDTO : constructeurs + getters/setters + equals/hashCode/toString")
	public void testInputDTOBeton() {

		final SousTypeProduitDTO.InputDTO dtoNull = new SousTypeProduitDTO.InputDTO();
		assertThat(dtoNull.getTypeProduit()).isNull();
		assertThat(dtoNull.getSousTypeProduit()).isNull();

		final SousTypeProduitDTO.InputDTO dto1 = new SousTypeProduitDTO.InputDTO(VETEMENT, HOMME);
		assertThat(dto1.getTypeProduit()).isEqualTo(VETEMENT);
		assertThat(dto1.getSousTypeProduit()).isEqualTo(HOMME);

		/*
		 * Vérifie la mutabilité nominale du DTO d'entrée
		 * au travers des setters publics.
		 */
		dto1.setTypeProduit(PECHE);
		dto1.setSousTypeProduit(CANNE);

		assertThat(dto1.getTypeProduit()).isEqualTo(PECHE);
		assertThat(dto1.getSousTypeProduit()).isEqualTo(CANNE);

		final SousTypeProduitDTO.InputDTO dto2 = new SousTypeProduitDTO.InputDTO(PECHE, CANNE);

		/*
		 * Vérifie la cohérence du couple equals/hashCode
		 * lorsque les deux champs métier sont identiques.
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
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>Vérifie le comportement nominal de <code>OutputDTO</code> :</p>
	 * <ul>
	 * <li>priorité de l'ID dans <code>equals/hashCode</code> ;</li>
	 * <li>fallback métier sur <code>(typeProduit, sousTypeProduit)</code>
	 * quand les deux IDs sont nuls ;</li>
	 * <li>inégalité si le fallback métier ne matche pas ;</li>
	 * <li>mutabilité via les setters ;</li>
	 * <li>présence des informations utiles dans <code>toString()</code>.</li>
	 * </ul>
	 * </div>
	 */
	@Test
	@DisplayName("SousTypeProduitDTO.OutputDTO : equals/hashCode ID-first puis fallback (typeProduit+sousTypeProduit) + liste produits")
	public void testOutputDTOBeton() {

		final List<String> produits1 = new ArrayList<String>();
		produits1.add("Chemise");
		produits1.add("Pantalon");

		final List<String> produits2 = new ArrayList<String>();
		produits2.add("Veste");

		/*
		 * Cas ID-first :
		 * même ID => égalité vraie,
		 * même si les autres champs diffèrent.
		 */
		final SousTypeProduitDTO.OutputDTO o1 =
				new SousTypeProduitDTO.OutputDTO(10L, VETEMENT, HOMME, produits1);

		final SousTypeProduitDTO.OutputDTO o2 =
				new SousTypeProduitDTO.OutputDTO(10L, PECHE, CANNE, produits2);

		assertThat(o1).isEqualTo(o2);
		assertThat(o2).isEqualTo(o1);
		assertThat(o1.hashCode()).isEqualTo(o2.hashCode());

		/*
		 * Cas fallback métier :
		 * les deux IDs sont nuls,
		 * l'égalité repose donc sur le couple
		 * (typeProduit, sousTypeProduit).
		 */
		final SousTypeProduitDTO.OutputDTO o3 =
				new SousTypeProduitDTO.OutputDTO(null, VETEMENT, HOMME, produits1);

		final SousTypeProduitDTO.OutputDTO o4 =
				new SousTypeProduitDTO.OutputDTO(null, VETEMENT, HOMME, produits2);

		assertThat(o3).isEqualTo(o4);
		assertThat(o4).isEqualTo(o3);
		assertThat(o3.hashCode()).isEqualTo(o4.hashCode());

		/*
		 * Toujours en fallback métier :
		 * un sous-type différent doit produire une inégalité.
		 */
		final SousTypeProduitDTO.OutputDTO o5 =
				new SousTypeProduitDTO.OutputDTO(null, VETEMENT, FEMME, produits1);

		assertThat(o3).isNotEqualTo(o5);
		assertThat(o5).isNotEqualTo(o3);

		/*
		 * Toujours en fallback métier :
		 * un type parent différent doit produire une inégalité.
		 */
		final SousTypeProduitDTO.OutputDTO o6 =
				new SousTypeProduitDTO.OutputDTO(null, PECHE, HOMME, produits1);

		assertThat(o3).isNotEqualTo(o6);
		assertThat(o6).isNotEqualTo(o3);

		/*
		 * Vérifie la mutabilité via les setters
		 * et l'exposition directe de la liste.
		 */
		final SousTypeProduitDTO.OutputDTO o7 = new SousTypeProduitDTO.OutputDTO();
		o7.setIdSousTypeProduit(20L);
		o7.setTypeProduit(RESTAURATION);
		o7.setSousTypeProduit(ENTREE);
		o7.setProduits(produits1);

		assertThat(o7.getIdSousTypeProduit()).isEqualTo(20L);
		assertThat(o7.getTypeProduit()).isEqualTo(RESTAURATION);
		assertThat(o7.getSousTypeProduit()).isEqualTo(ENTREE);
		assertThat(o7.getProduits()).isSameAs(produits1);

		/*
		 * Vérifie la forme utile de toString().
		 */
		final String toString = o7.toString();
		assertThat(toString).contains("OutputDTO");
		assertThat(toString).contains(RESTAURATION);
		assertThat(toString).contains(ENTREE);

		/*
		 * Robustesse minimale de equals(...).
		 */
		assertThat(o7).isNotEqualTo(null);
		assertThat(o7).isNotEqualTo(ENTREE);
		
	} // __________________________________________________________________
	
	

	/**
	 * <div>
	 * <p>Vérifie le cas critique corrigé dans <code>OutputDTO.equals(...)</code> :</p>
	 * <ul>
	 * <li>si un objet possède un ID non nul ;</li>
	 * <li>et l'autre un ID nul ;</li>
	 * <li>alors les deux objets doivent être inégaux,</li>
	 * <li>même si <code>typeProduit</code> et
	 * <code>sousTypeProduit</code> sont identiques.</li>
	 * </ul>
	 * 
	 * <p>
	 * Ce test verrouille la cohérence entre
	 * <code>equals(...)</code> et <code>hashCode()</code>.
	 * </p>
	 * </div>
	 */
	@Test
	@DisplayName("SousTypeProduitDTO.OutputDTO : id non null vs id null => inégalité même si typeProduit et sousTypeProduit identiques")
	public void testOutputDTOIdNonNullVsIdNullDoitEtreInegal() {

		final List<String> produits1 = new ArrayList<String>();
		produits1.add("Chemise");
		produits1.add("Pantalon");

		final List<String> produits2 = new ArrayList<String>();
		produits2.add("Veste");

		/*
		 * Même identité métier apparente,
		 * mais asymétrie de présence d'ID :
		 * o1 a un ID technique,
		 * o2 n'en a pas.
		 */
		final SousTypeProduitDTO.OutputDTO o1 =
				new SousTypeProduitDTO.OutputDTO(10L, VETEMENT, HOMME, produits1);

		final SousTypeProduitDTO.OutputDTO o2 =
				new SousTypeProduitDTO.OutputDTO(null, VETEMENT, HOMME, produits2);

		/*
		 * Le contrat attendu est l'inégalité stricte
		 * pour préserver la cohérence avec hashCode().
		 */
		assertThat(o1).isNotEqualTo(o2);
		assertThat(o2).isNotEqualTo(o1);
		
	} // __________________________________________________________________
	
	

}