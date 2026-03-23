package levy.daniel.application.model.dto.produittype;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import levy.daniel.application.model.metier.produittype.SousTypeProduit;
import levy.daniel.application.model.metier.produittype.SousTypeProduitI;
import levy.daniel.application.model.metier.produittype.TypeProduit;

/**
 * <style>p, ul, li, h1 {line-height : 1em;}</style> <style>h1
 * {text-decoration: underline;}</style>
 * 
 * <div> </div>
 *
 *
 * @author Daniel Lévy
 * @version 1.0
 * @since 16 janv. 2026
 */
public class ConvertisseurMetierToOutputDTOTypeProduitTest {

	/**
	 * "vêtement"
	 */
	public static final String VETEMENT = "vêtement";

	/**
	 * "vêtement pour homme"
	 */
	public static final String VETEMENT_POUR_HOMME = "vêtement pour homme";

	/**
	 * "dto-convertisseur"
	 */
	public static final String DTO_CONVERTISSEUR = "dto-convertisseur";


	/**
	 *<div>
	 * <p>
	 * CONSTRUCTEUR D'ARITE NULLE.
	 * </p>
	 * </div>
	 * */
	public ConvertisseurMetierToOutputDTOTypeProduitTest() {
		super();
	}



	/**
	 * convert(null) retourne null
	 */
	@Test
	@Tag(DTO_CONVERTISSEUR)
	@DisplayName("convert(null) retourne null")
	public void testConvertNull() {

		// ACT
		final TypeProduitDTO.OutputDTO dto = ConvertisseurMetierToOutputDTOTypeProduit
				.convert(null);

		// ASSERT
		assertThat(dto).isNull();

	}



	/**
	 * convert(TypeProduit) copie id/typeProduit et retourne 
	 * une liste vide si aucun SousTypeProduit
	 */
	@Test
	@Tag(DTO_CONVERTISSEUR)
	@DisplayName("convert(TypeProduit) copie id/typeProduit et retourne une liste vide si aucun SousTypeProduit")
	public void testConvertSansSousTypes() {

		// ARRANGE
		final TypeProduit tp = new TypeProduit();
		tp.setIdTypeProduit(1L);
		tp.setTypeProduit(VETEMENT);

		// ACT
		final TypeProduitDTO.OutputDTO dto = ConvertisseurMetierToOutputDTOTypeProduit
				.convert(tp);

		// ASSERT
		assertThat(dto).isNotNull();
		assertThat(dto.getIdTypeProduit()).isEqualTo(1L);
		assertThat(dto.getTypeProduit()).isEqualTo(VETEMENT);
		assertThat(dto.getSousTypeProduits()).isNotNull();
		assertThat(dto.getSousTypeProduits()).isEmpty();

	}



	/**
	 * convert(TypeProduit) extrait les sous-types non nuls et 
	 * ignore les libellés null
	 *
	 */
	@Test
	@Tag(DTO_CONVERTISSEUR)
	@DisplayName("convert(TypeProduit) extrait les sous-types non nuls et ignore les libellés null")
	public void testConvertFiltreNulls() {

		// ARRANGE
		final TypeProduit tp = new TypeProduit();
		tp.setIdTypeProduit(10L);
		tp.setTypeProduit(VETEMENT);

		final SousTypeProduitI stp1 = new SousTypeProduit();
		stp1.setSousTypeProduit(VETEMENT_POUR_HOMME);

		final SousTypeProduitI stp2 = new SousTypeProduit();
		stp2.setSousTypeProduit(null); // doit être ignoré

		// rattachement canonique au parent (utilise ton API canonique)
		tp.rattacherEnfantSTP(stp1);
		tp.rattacherEnfantSTP(stp2);

		// ACT
		final TypeProduitDTO.OutputDTO dto = ConvertisseurMetierToOutputDTOTypeProduit
				.convert(tp);

		// ASSERT
		assertThat(dto).isNotNull();
		assertThat(dto.getIdTypeProduit()).isEqualTo(10L);
		assertThat(dto.getTypeProduit()).isEqualTo(VETEMENT);

		final List<String> sousTypes = dto.getSousTypeProduits();
		assertThat(sousTypes).isNotNull()
				.containsExactly(VETEMENT_POUR_HOMME); // stp2 ignoré car
														// libellé null

	}



	/**
	 * convert(TypeProduit) conserve l'ordre des SousTypeProduits
	 */
	@Test
	@Tag(DTO_CONVERTISSEUR)
	@DisplayName("convert(TypeProduit) conserve l'ordre des SousTypeProduits")
	public void testConvertConserveOrdre() {

		// ARRANGE
		final TypeProduit tp = new TypeProduit();
		tp.setIdTypeProduit(2L);
		tp.setTypeProduit(VETEMENT);

		final SousTypeProduitI homme = new SousTypeProduit();
		homme.setSousTypeProduit(VETEMENT_POUR_HOMME);

		final SousTypeProduitI femme = new SousTypeProduit();
		femme.setSousTypeProduit("vêtement pour femme");

		final SousTypeProduitI enfant = new SousTypeProduit();
		enfant.setSousTypeProduit("vêtement pour enfant");

		tp.rattacherEnfantSTP(homme);
		tp.rattacherEnfantSTP(femme);
		tp.rattacherEnfantSTP(enfant);

		// ACT
		final TypeProduitDTO.OutputDTO dto = ConvertisseurMetierToOutputDTOTypeProduit
				.convert(tp);

		// ASSERT
		assertThat(dto).isNotNull();
		assertThat(dto.getSousTypeProduits()).containsExactly(
				VETEMENT_POUR_HOMME, "vêtement pour femme",
				"vêtement pour enfant");

	}



	/**
	 * OutputDTO equals/hashCode: si les 2 IDs sont non nuls
	 * , l'égalité est basée sur l'ID 
	 * (typeProduit et sousTypeProduits ignorés)
	 */
	@Test
	@Tag("dto-egalite")
	@DisplayName("OutputDTO equals/hashCode: si les 2 IDs sont non nuls, l'égalité est basée sur l'ID (typeProduit et sousTypeProduits ignorés)")
	public void testOutputDTOEqualsIdFirst() {

		final TypeProduitDTO.OutputDTO dto1 = new TypeProduitDTO.OutputDTO(
				1L, VETEMENT, List.of("A"));

		final TypeProduitDTO.OutputDTO dto2 = new TypeProduitDTO.OutputDTO(
				1L, "autre", List.of("B", "C"));

		assertThat(dto1).isEqualTo(dto2);
		assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

	}



	/**
	 * OutputDTO equals/hashCode: si un ID est null, 
	 * l'égalité fallback sur typeProduit
	 */
	@Test
	@Tag("dto-egalite")
	@DisplayName("OutputDTO equals/hashCode: si un ID est null, l'égalité fallback sur typeProduit")
	public void testOutputDTOEqualsFallbackTypeProduit() {

		final TypeProduitDTO.OutputDTO dto1 = new TypeProduitDTO.OutputDTO(
				null, VETEMENT, List.of("A"));

		final TypeProduitDTO.OutputDTO dto2 = new TypeProduitDTO.OutputDTO(
				null, VETEMENT, List.of("X", "Y"));

		final TypeProduitDTO.OutputDTO dto3 = new TypeProduitDTO.OutputDTO(
				null, "bazar", List.of("A"));

		assertThat(dto1).isEqualTo(dto2);
		assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
		assertThat(dto1).isNotEqualTo(dto3);

	}



	/**
	 * Dédoublonnage Set: 2 DTO de même ID ne doivent apparaître 
	 * qu'une fois (LinkedHashSet)
	 */
	@Test
	@Tag("dto-dedoublonnage")
	@DisplayName("Dédoublonnage Set: 2 DTO de même ID ne doivent apparaître qu'une fois (LinkedHashSet)")
	public void testDedoublonnageSetParId() {

		final TypeProduitDTO.OutputDTO dto1 = new TypeProduitDTO.OutputDTO(
				1L, VETEMENT, List.of("A"));

		final TypeProduitDTO.OutputDTO dto2 = new TypeProduitDTO.OutputDTO(
				1L, "vêtement bis", List.of("B"));

		final Set<TypeProduitDTO.OutputDTO> uniques = new LinkedHashSet<TypeProduitDTO.OutputDTO>();

		uniques.add(dto1);
		uniques.add(dto2);

		assertThat(uniques).hasSize(1);
		assertThat(uniques.iterator().next().getIdTypeProduit())
				.isEqualTo(1L);

	}



	/**
	 * convert(TypeProduit) ignore un SousTypeProduit null (robustesse)
	 */
	@Test
	@Tag(DTO_CONVERTISSEUR)
	@DisplayName("convert(TypeProduit) ignore un SousTypeProduit null (robustesse)")
	public void testConvertIgnoreSousTypeProduitNull() {

		// ARRANGE
		final TypeProduit tp = new TypeProduit();
		tp.setIdTypeProduit(11L);
		tp.setTypeProduit(VETEMENT);

		final SousTypeProduitI stp1 = new SousTypeProduit();
		stp1.setSousTypeProduit(VETEMENT_POUR_HOMME);

		tp.rattacherEnfantSTP(stp1);

		// injection d'un element null dans la collection interne (cas
		// robuste)
		final List<SousTypeProduitI> listeHack = new ArrayList<SousTypeProduitI>(
				tp.getSousTypeProduits());
		listeHack.add(null);
		tp.setSousTypeProduits(listeHack);

		// ACT
		final TypeProduitDTO.OutputDTO dto = ConvertisseurMetierToOutputDTOTypeProduit
				.convert(tp);

		// ASSERT
		assertThat(dto).isNotNull();
		assertThat(dto.getSousTypeProduits())
				.containsExactly(VETEMENT_POUR_HOMME);

	}

}
