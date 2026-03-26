package levy.daniel.application.model.dto.produittype;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.SousTypeProduitI;
import levy.daniel.application.model.metier.produittype.TypeProduit;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style>
 * <style>h1 {text-decoration: underline;}</style>
 *
 * <div>
 * <p style="font-weight:bold;">
 * CLASSE ConvertisseurMetierToOutputDTOTypeProduitTest.java :
 * </p>
 *
 * <p>
 * Tests béton pour le convertisseur
 * {@link ConvertisseurMetierToOutputDTOTypeProduit}.
 * </p>
 *
 * <p>
 * Cette classe vérifie :
 * </p>
 * <ul>
 * <li>la structure utilitaire du convertisseur ;</li>
 * <li>le comportement nominal de {@code convert(TypeProduit)} ;</li>
 * <li>le filtrage des enfants null et des libellés null ;</li>
 * <li>la conservation de l'ordre des sous-types ;</li>
 * <li>la mutabilité homogène de la liste vide retournée ;</li>
 * <li>le comportement de {@code convertList(...)} ;</li>
 * <li>le dédoublonnage par ID ou par clé métier DTO ;</li>
 * <li>le cas critique d'inégalité DTO quand un seul ID est présent.</li>
 * </ul>
 * </div>
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 16 janvier 2026
 */
public class ConvertisseurMetierToOutputDTOTypeProduitTest {

	/* ************************ CONSTANTES ******************************** */

	/**
	 * "vêtement"
	 */
	public static final String VETEMENT = "vêtement";

	/**
	 * "outillage"
	 */
	public static final String OUTILLAGE = "outillage";

	/**
	 * "vêtement pour homme"
	 */
	public static final String VETEMENT_POUR_HOMME = "vêtement pour homme";

	/**
	 * "vêtement pour femme"
	 */
	public static final String VETEMENT_POUR_FEMME = "vêtement pour femme";

	/**
	 * "vêtement pour enfant"
	 */
	public static final String VETEMENT_POUR_ENFANT = "vêtement pour enfant";

	/**
	 * "dto-convertisseur"
	 */
	public static final String DTO_CONVERTISSEUR = "dto-convertisseur";

	/**
	 * "dto-egalite"
	 */
	public static final String DTO_EGALITE = "dto-egalite";

	/**
	 * "dto-dedoublonnage"
	 */
	public static final String DTO_DEDOUBLONNAGE = "dto-dedoublonnage";

	/**
	 * "A"
	 */
	public static final String A = "A";

	/**
	 * "B"
	 */
	public static final String B = "B";

	/**
	 * "C"
	 */
	public static final String C = "C";


	/* ************************ CONSTRUCTEUR ****************************** */

	/**
	 * <div>
	 * <p>CONSTRUCTEUR D'ARITE NULLE.</p>
	 * </div>
	 */
	public ConvertisseurMetierToOutputDTOTypeProduitTest() {
		super();
	}


	/* ************************ METHODES ********************************** */

	
	/**
	 * <div>
	 * <p>Vérifie que le convertisseur est bien une classe utilitaire :</p>
	 * <ul>
	 * <li>classe <code>final</code> ;</li>
	 * <li>constructeur unique ;</li>
	 * <li>constructeur <code>private</code>.</li>
	 * </ul>
	 * </div>
	 *
	 * @throws Exception en cas d'échec de réflexion.
	 */
	@Test
	@Tag(DTO_CONVERTISSEUR)
	@DisplayName("ConvertisseurMetierToOutputDTOTypeProduit : classe utilitaire final + constructeur privé")
	public void testStructureClasseUtilitaire() throws Exception {

		assertThat(
				Modifier.isFinal(
						ConvertisseurMetierToOutputDTOTypeProduit.class
								.getModifiers()))
				.isTrue();

		final Constructor<?>[] constructeurs =
				ConvertisseurMetierToOutputDTOTypeProduit.class
						.getDeclaredConstructors();

		assertThat(constructeurs).isNotNull();
		assertThat(constructeurs).hasSize(1);

		final Constructor<?> constructeur = constructeurs[0];

		assertThat(Modifier.isPrivate(constructeur.getModifiers())).isTrue();

		/*
		 * Vérifie qu'il reste possible d'instancier la classe
		 * par réflexion pour couvrir le constructeur privé.
		 */
		constructeur.setAccessible(true); // NOPMD by danyl
		final Object instance = constructeur.newInstance();

		assertThat(instance).isNotNull();

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>Vérifie que {@code convert(null)} retourne {@code null}.</p>
	 * </div>
	 */
	@Test
	@Tag(DTO_CONVERTISSEUR)
	@DisplayName("convert(null) retourne null")
	public void testConvertNull() {

		/*
		 * ACT
		 */
		final TypeProduitDTO.OutputDTO dto =
				ConvertisseurMetierToOutputDTOTypeProduit.convert(null);

		/*
		 * ASSERT
		 */
		assertThat(dto).isNull();

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>Vérifie que {@code convert(TypeProduit)} copie l'ID et le libellé
	 * métier, et retourne une liste vide si aucun sous-type n'est présent.</p>
	 * </div>
	 */
	@Test
	@Tag(DTO_CONVERTISSEUR)
	@DisplayName("convert(TypeProduit) copie id/typeProduit et retourne une liste vide si aucun SousTypeProduit")
	public void testConvertSansSousTypes() {

		/*
		 * ARRANGE
		 */
		final TypeProduit tp = new TypeProduit();
		tp.setIdTypeProduit(1L);
		tp.setTypeProduit(VETEMENT);

		/*
		 * ACT
		 */
		final TypeProduitDTO.OutputDTO dto =
				ConvertisseurMetierToOutputDTOTypeProduit.convert(tp);

		/*
		 * ASSERT
		 */
		assertThat(dto).isNotNull();
		assertThat(dto.getIdTypeProduit()).isEqualTo(1L);
		assertThat(dto.getTypeProduit()).isEqualTo(VETEMENT);
		assertThat(dto.getSousTypeProduits()).isNotNull();
		assertThat(dto.getSousTypeProduits()).isEmpty();

	} // __________________________________________________________________

	

	/**
	 * <div>
	 * <p>Vérifie que la liste vide retournée pour les sous-types
	 * est bien mutable, afin de verrouiller l'homogénéité de mutabilité
	 * introduite dans le convertisseur.</p>
	 * </div>
	 */
	@Test
	@Tag(DTO_CONVERTISSEUR)
	@DisplayName("convert(TypeProduit) retourne une liste vide mutable si aucun SousTypeProduit")
	public void testConvertSansSousTypesRetourneListeVideMutable() {

		/*
		 * ARRANGE
		 */
		final TypeProduit tp = new TypeProduit();
		tp.setIdTypeProduit(2L);
		tp.setTypeProduit(VETEMENT);

		/*
		 * ACT
		 */
		final TypeProduitDTO.OutputDTO dto =
				ConvertisseurMetierToOutputDTOTypeProduit.convert(tp);

		final List<String> sousTypes = dto.getSousTypeProduits();
		sousTypes.add(VETEMENT_POUR_HOMME);

		/*
		 * ASSERT
		 */
		assertThat(sousTypes).containsExactly(VETEMENT_POUR_HOMME);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>Vérifie que {@code convert(TypeProduit)} extrait les sous-types
	 * non nuls et ignore les libellés null.</p>
	 * </div>
	 */
	@Test
	@Tag(DTO_CONVERTISSEUR)
	@DisplayName("convert(TypeProduit) extrait les sous-types non nuls et ignore les libellés null")
	public void testConvertFiltreNulls() {

		/*
		 * ARRANGE
		 */
		final TypeProduit tp = new TypeProduit();
		tp.setIdTypeProduit(10L);
		tp.setTypeProduit(VETEMENT);

		final SousTypeProduitI stp1 = new SousTypeProduit();
		stp1.setSousTypeProduit(VETEMENT_POUR_HOMME);

		final SousTypeProduitI stp2 = new SousTypeProduit();
		stp2.setSousTypeProduit(null);

		/*
		 * Rattachement canonique au parent.
		 */
		tp.rattacherEnfantSTP(stp1);
		tp.rattacherEnfantSTP(stp2);

		/*
		 * ACT
		 */
		final TypeProduitDTO.OutputDTO dto =
				ConvertisseurMetierToOutputDTOTypeProduit.convert(tp);

		/*
		 * ASSERT
		 */
		assertThat(dto).isNotNull();
		assertThat(dto.getIdTypeProduit()).isEqualTo(10L);
		assertThat(dto.getTypeProduit()).isEqualTo(VETEMENT);

		final List<String> sousTypes = dto.getSousTypeProduits();
		assertThat(sousTypes).isNotNull();
		assertThat(sousTypes).containsExactly(VETEMENT_POUR_HOMME);

	} // __________________________________________________________________

	

	/**
	 * <div>
	 * <p>Vérifie que {@code convert(TypeProduit)} conserve l'ordre
	 * des sous-types enfants.</p>
	 * </div>
	 */
	@Test
	@Tag(DTO_CONVERTISSEUR)
	@DisplayName("convert(TypeProduit) conserve l'ordre des SousTypeProduits")
	public void testConvertConserveOrdre() {

		/*
		 * ARRANGE
		 */
		final TypeProduit tp = new TypeProduit();
		tp.setIdTypeProduit(3L);
		tp.setTypeProduit(VETEMENT);

		final SousTypeProduitI homme = new SousTypeProduit();
		homme.setSousTypeProduit(VETEMENT_POUR_HOMME);

		final SousTypeProduitI femme = new SousTypeProduit();
		femme.setSousTypeProduit(VETEMENT_POUR_FEMME);

		final SousTypeProduitI enfant = new SousTypeProduit();
		enfant.setSousTypeProduit(VETEMENT_POUR_ENFANT);

		tp.rattacherEnfantSTP(homme);
		tp.rattacherEnfantSTP(femme);
		tp.rattacherEnfantSTP(enfant);

		/*
		 * ACT
		 */
		final TypeProduitDTO.OutputDTO dto =
				ConvertisseurMetierToOutputDTOTypeProduit.convert(tp);

		/*
		 * ASSERT
		 */
		assertThat(dto).isNotNull();
		assertThat(dto.getSousTypeProduits()).containsExactly(
				VETEMENT_POUR_HOMME,
				VETEMENT_POUR_FEMME,
				VETEMENT_POUR_ENFANT);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>Vérifie que {@code convert(TypeProduit)} ignore un enfant null
	 * injecté artificiellement dans la collection interne.</p>
	 * </div>
	 */
	@Test
	@Tag(DTO_CONVERTISSEUR)
	@DisplayName("convert(TypeProduit) ignore un SousTypeProduit null (robustesse)")
	public void testConvertIgnoreSousTypeProduitNull() {

		/*
		 * ARRANGE
		 */
		final TypeProduit tp = new TypeProduit();
		tp.setIdTypeProduit(11L);
		tp.setTypeProduit(VETEMENT);

		final SousTypeProduitI stp1 = new SousTypeProduit();
		stp1.setSousTypeProduit(VETEMENT_POUR_HOMME);

		tp.rattacherEnfantSTP(stp1);

		/*
		 * Injection d'un élément null dans la collection interne
		 * pour verrouiller la robustesse du convertisseur.
		 */
		final List<SousTypeProduitI> listeHack =
				new ArrayList<SousTypeProduitI>(tp.getSousTypeProduits());
		listeHack.add(null);
		tp.setSousTypeProduits(listeHack);

		/*
		 * ACT
		 */
		final TypeProduitDTO.OutputDTO dto =
				ConvertisseurMetierToOutputDTOTypeProduit.convert(tp);

		/*
		 * ASSERT
		 */
		assertThat(dto).isNotNull();
		assertThat(dto.getSousTypeProduits())
				.containsExactly(VETEMENT_POUR_HOMME);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>Vérifie que {@code convertList(null)} retourne {@code null}.</p>
	 * </div>
	 */
	@Test
	@Tag(DTO_CONVERTISSEUR)
	@DisplayName("convertList(null) retourne null")
	public void testConvertListNull() {

		/*
		 * ACT
		 */
		final List<TypeProduitDTO.OutputDTO> resultat =
				ConvertisseurMetierToOutputDTOTypeProduit.convertList(null);

		/*
		 * ASSERT
		 */
		assertThat(resultat).isNull();

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>Vérifie que {@code convertList(...)} retourne une liste vide
	 * si la liste source est vide.</p>
	 * </div>
	 */
	@Test
	@Tag(DTO_CONVERTISSEUR)
	@DisplayName("convertList(liste vide) retourne une liste vide")
	public void testConvertListVide() {

		/*
		 * ARRANGE
		 */
		final List<TypeProduit> source = new ArrayList<TypeProduit>();

		/*
		 * ACT
		 */
		final List<TypeProduitDTO.OutputDTO> resultat =
				ConvertisseurMetierToOutputDTOTypeProduit.convertList(source);

		/*
		 * ASSERT
		 */
		assertThat(resultat).isNotNull();
		assertThat(resultat).isEmpty();

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>Vérifie que {@code convertList(...)} ignore les éléments null
	 * et conserve l'ordre de première apparition.</p>
	 * </div>
	 */
	@Test
	@Tag(DTO_CONVERTISSEUR)
	@DisplayName("convertList(...) ignore les éléments null et conserve l'ordre")
	public void testConvertListIgnoreNullEtConserveOrdre() {

		/*
		 * ARRANGE
		 */
		final TypeProduit tp1 = new TypeProduit();
		tp1.setIdTypeProduit(1L);
		tp1.setTypeProduit(VETEMENT);

		final TypeProduit tp2 = new TypeProduit();
		tp2.setIdTypeProduit(2L);
		tp2.setTypeProduit(OUTILLAGE);

		final List<TypeProduit> source = new ArrayList<TypeProduit>();
		source.add(null);
		source.add(tp1);
		source.add(tp2);

		/*
		 * ACT
		 */
		final List<TypeProduitDTO.OutputDTO> resultat =
				ConvertisseurMetierToOutputDTOTypeProduit.convertList(source);

		/*
		 * ASSERT
		 */
		assertThat(resultat).hasSize(2);
		assertThat(resultat.get(0).getIdTypeProduit()).isEqualTo(1L);
		assertThat(resultat.get(0).getTypeProduit()).isEqualTo(VETEMENT);
		assertThat(resultat.get(1).getIdTypeProduit()).isEqualTo(2L);
		assertThat(resultat.get(1).getTypeProduit()).isEqualTo(OUTILLAGE);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>Vérifie que {@code convertList(...)} dédoublonne sur l'ID
	 * quand deux objets convertis portent le même identifiant technique.</p>
	 * </div>
	 */
	@Test
	@Tag(DTO_DEDOUBLONNAGE)
	@DisplayName("convertList(...) dédoublonne par ID et conserve la première occurrence")
	public void testConvertListDedoublonneParId() {

		/*
		 * ARRANGE
		 */
		final TypeProduit tp1 = new TypeProduit();
		tp1.setIdTypeProduit(1L);
		tp1.setTypeProduit(VETEMENT);

		final TypeProduit tp2 = new TypeProduit();
		tp2.setIdTypeProduit(1L);
		tp2.setTypeProduit(OUTILLAGE);

		final List<TypeProduit> source = new ArrayList<TypeProduit>();
		source.add(tp1);
		source.add(tp2);

		/*
		 * ACT
		 */
		final List<TypeProduitDTO.OutputDTO> resultat =
				ConvertisseurMetierToOutputDTOTypeProduit.convertList(source);

		/*
		 * ASSERT
		 * La première occurrence doit être conservée
		 * car le LinkedHashSet conserve l'ordre d'insertion.
		 */
		assertThat(resultat).hasSize(1);
		assertThat(resultat.get(0).getIdTypeProduit()).isEqualTo(1L);
		assertThat(resultat.get(0).getTypeProduit()).isEqualTo(VETEMENT);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>Vérifie que {@code convertList(...)} dédoublonne sur le libellé
	 * métier quand les deux IDs sont nuls.</p>
	 *
	 * <p>
	 * Ce comportement est cohérent avec la règle
	 * {@code equals/hashCode} de {@code TypeProduitDTO.OutputDTO},
	 * fondée sur {@code typeProduit} quand les deux IDs sont absents.
	 * </p>
	 * </div>
	 */
	@Test
	@Tag(DTO_DEDOUBLONNAGE)
	@DisplayName("convertList(...) dédoublonne par typeProduit quand les IDs sont nuls")
	public void testConvertListDedoublonneParTypeProduitQuandIdsNuls() {

		/*
		 * ARRANGE
		 */
		final TypeProduit tp1 = new TypeProduit();
		tp1.setTypeProduit(VETEMENT);

		final SousTypeProduitI stp1 = new SousTypeProduit();
		stp1.setSousTypeProduit(VETEMENT_POUR_HOMME);
		tp1.rattacherEnfantSTP(stp1);

		final TypeProduit tp2 = new TypeProduit();
		tp2.setTypeProduit(VETEMENT);

		final SousTypeProduitI stp2 = new SousTypeProduit();
		stp2.setSousTypeProduit(VETEMENT_POUR_FEMME);
		tp2.rattacherEnfantSTP(stp2);

		final List<TypeProduit> source = new ArrayList<TypeProduit>();
		source.add(tp1);
		source.add(tp2);

		/*
		 * ACT
		 */
		final List<TypeProduitDTO.OutputDTO> resultat =
				ConvertisseurMetierToOutputDTOTypeProduit.convertList(source);

		/*
		 * ASSERT
		 * La première occurrence doit être conservée.
		 */
		assertThat(resultat).hasSize(1);
		assertThat(resultat.get(0).getIdTypeProduit()).isNull();
		assertThat(resultat.get(0).getTypeProduit()).isEqualTo(VETEMENT);
		assertThat(resultat.get(0).getSousTypeProduits())
				.containsExactly(VETEMENT_POUR_HOMME);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>Vérifie la règle DTO : si les deux IDs sont non nuls,
	 * l'égalité repose exclusivement sur l'ID.</p>
	 * </div>
	 */
	@Test
	@Tag(DTO_EGALITE)
	@DisplayName("OutputDTO equals/hashCode : si les 2 IDs sont non nuls, l'égalité est basée sur l'ID")
	public void testOutputDTOEqualsIdFirst() {

		/*
		 * ARRANGE
		 */
		final List<String> sousTypes1 = new ArrayList<String>();
		sousTypes1.add(A);

		final List<String> sousTypes2 = new ArrayList<String>();
		sousTypes2.add(B);
		sousTypes2.add(C);

		final TypeProduitDTO.OutputDTO dto1 =
				new TypeProduitDTO.OutputDTO(1L, VETEMENT, sousTypes1);

		final TypeProduitDTO.OutputDTO dto2 =
				new TypeProduitDTO.OutputDTO(1L, "autre", sousTypes2);

		/*
		 * ASSERT
		 */
		assertThat(dto1).isEqualTo(dto2);
		assertThat(dto2).isEqualTo(dto1);
		assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>Vérifie la règle DTO : si les deux IDs sont nuls,
	 * l'égalité fallback repose sur <code>typeProduit</code>.</p>
	 * </div>
	 */
	@Test
	@Tag(DTO_EGALITE)
	@DisplayName("OutputDTO equals/hashCode : si les 2 IDs sont nuls, l'égalité fallback sur typeProduit")
	public void testOutputDTOEqualsFallbackTypeProduit() {

		/*
		 * ARRANGE
		 */
		final List<String> sousTypes1 = new ArrayList<String>();
		sousTypes1.add(A);

		final List<String> sousTypes2 = new ArrayList<String>();
		sousTypes2.add(B);
		sousTypes2.add(C);

		final TypeProduitDTO.OutputDTO dto1 =
				new TypeProduitDTO.OutputDTO(null, VETEMENT, sousTypes1);

		final TypeProduitDTO.OutputDTO dto2 =
				new TypeProduitDTO.OutputDTO(null, VETEMENT, sousTypes2);

		final TypeProduitDTO.OutputDTO dto3 =
				new TypeProduitDTO.OutputDTO(null, "bazar", sousTypes1);

		/*
		 * ASSERT
		 */
		assertThat(dto1).isEqualTo(dto2);
		assertThat(dto2).isEqualTo(dto1);
		assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
		assertThat(dto1).isNotEqualTo(dto3);
		assertThat(dto3).isNotEqualTo(dto1);

	} // __________________________________________________________________


	
	/**
	 * <div>
	 * <p>Vérifie le cas critique corrigé dans le DTO :</p>
	 * <ul>
	 * <li>un DTO avec ID non nul ;</li>
	 * <li>un DTO avec ID nul ;</li>
	 * <li>même <code>typeProduit</code> ;</li>
	 * <li>résultat attendu : inégalité stricte.</li>
	 * </ul>
	 *
	 * <p>
	 * Ce test verrouille la cohérence entre
	 * <code>equals(...)</code> et <code>hashCode()</code>.
	 * </p>
	 * </div>
	 */
	@Test
	@Tag(DTO_EGALITE)
	@DisplayName("OutputDTO equals/hashCode : id non null vs id null => inégalité même si typeProduit identique")
	public void testOutputDTOIdNonNullVsIdNullDoitEtreInegal() {

		/*
		 * ARRANGE
		 */
		final List<String> sousTypes1 = new ArrayList<String>();
		sousTypes1.add(A);

		final List<String> sousTypes2 = new ArrayList<String>();
		sousTypes2.add(B);

		final TypeProduitDTO.OutputDTO dto1 =
				new TypeProduitDTO.OutputDTO(1L, VETEMENT, sousTypes1);

		final TypeProduitDTO.OutputDTO dto2 =
				new TypeProduitDTO.OutputDTO(null, VETEMENT, sousTypes2);

		/*
		 * ASSERT
		 */
		assertThat(dto1).isNotEqualTo(dto2);
		assertThat(dto2).isNotEqualTo(dto1);

	} // __________________________________________________________________

	
	
}